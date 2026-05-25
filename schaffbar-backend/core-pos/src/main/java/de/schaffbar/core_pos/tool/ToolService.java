package de.schaffbar.core_pos.tool;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEventRepository;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateWlanRelaisCommand;
import de.schaffbar.core_pos.tool.ToolViews.ToolView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ToolService {

    private final @NonNull ToolRepository toolRepository;

    private final @NonNull OutboxEventRepository outboxEventRepository;

    // ------------------------------------------------------------------------
    // query

    public List<ToolView> getTools() {
        return this.toolRepository.findAll().stream() //
                .map(ToolViews.MAPPER::toToolView) //
                .toList();
    }

    public Optional<ToolView> getTool(@NotNull @Valid ToolId id) {
        return this.toolRepository.findById(id.getValue()) //
                .map(ToolViews.MAPPER::toToolView);
    }

    public Optional<ToolView> getTool(@NotNull @Valid RfidReaderId id) {
        return this.toolRepository.findByRfidReaderId(id) //
                .map(ToolViews.MAPPER::toToolView);
    }

    public boolean isInstructor(@NotNull @Valid ToolId toolId, @NotNull @Valid CustomerId customerId) {
        Tool tool = this.toolRepository.findById(toolId.getValue()) //
                .orElseThrow(() -> ResourceNotFoundException.tool(toolId));

        return tool.isInstructor(customerId);
    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public ToolId createTool(@NotNull @Valid CreateToolCommand command) {
        this.toolRepository.findByName(command.name()) //
                .ifPresent(existingTool -> throwToolNameAlreadyUsedException(command.name(), existingTool));

        if (nonNull(command.rfidReaderId())) {
            this.toolRepository.findByRfidReaderId(command.rfidReaderId()) //
                    .ifPresent(existingTool -> throwRfidReaderAlreadyAssignedException(command.rfidReaderId(), existingTool));
        }

        if (nonNull(command.ipAddress())) {
            this.toolRepository.findByIpAddress(command.ipAddress()) //
                    .ifPresent(existingTool -> throwIpAddressAlreadyUsedException(command.ipAddress(), existingTool));
        }

        Tool tool = Tool.of(command);
        Tool savedTool = this.toolRepository.save(tool);

        List<SchaffbarEvent> events = List.of(ToolEventFactory.toolCreated(tool));
        saveOutboxEvents(events);

        log.info("Created tool with id {} and published events {}", savedTool.getId(), events);

        return savedTool.getId();
    }

    @Transactional
    public void updateTool(@NotNull @Valid ToolId toolId, @NotNull @Valid UpdateToolCommand command) {
        Tool tool = this.toolRepository.findById(toolId.getValue()) //
                .orElseThrow(() -> ResourceNotFoundException.tool(toolId));

        List<SchaffbarEvent> events = tool.update(command);
        saveOutboxEvents(events);

        log.info("Updated tool with id {} and published events {}", toolId, events);
    }

    // TODO: introduce dedicated commands for setting and clearing the WLAN relais
    @Transactional
    public void updateWlanRelais(@NotNull @Valid ToolId toolId, @NotNull @Valid UpdateWlanRelaisCommand command) {
        Tool tool = this.toolRepository.findById(toolId.getValue()) //
                .orElseThrow(() -> ResourceNotFoundException.tool(toolId));

        if (nonNull(command.ipAddress())) {
            this.toolRepository.findByIpAddress(command.ipAddress()) //
                    .filter(existingTool -> isFalse(toolId.sameValueAs(existingTool.getId()))) //
                    .ifPresent(existingTool -> throwIpAddressAlreadyUsedException(command.ipAddress(), existingTool));
        }

        List<SchaffbarEvent> events = tool.updateWlanRelais(command);
        saveOutboxEvents(events);

        log.info("Updated WLAN relais of tool with id {} and published events {}", toolId, events);
    }

    @Transactional
    public void assignRfidReader(@NotNull @Valid ToolId toolId, @NotNull @Valid RfidReaderId rfidReaderId) {
        Tool tool = this.toolRepository.findById(toolId.getValue()) //
                .orElseThrow(() -> ResourceNotFoundException.tool(toolId));

        if (rfidReaderId.sameValueAs(tool.getRfidReaderId())) {
            return;
        }

        this.toolRepository.findByRfidReaderId(rfidReaderId) //
                .ifPresent(existingTool -> throwRfidReaderAlreadyAssignedException(rfidReaderId, existingTool));

        List<SchaffbarEvent> events = tool.assignRfidReader(rfidReaderId);
        saveOutboxEvents(events);

        log.info("Assigned RFID reader {} to tool {} and published events {}", rfidReaderId, toolId, events);
    }

    @Transactional
    public void clearRfidReader(@NotNull @Valid ToolId toolId) {
        Tool tool = this.toolRepository.findById(toolId.getValue()) //
                .orElseThrow(() -> ResourceNotFoundException.tool(toolId));

        List<SchaffbarEvent> events = tool.clearRfidReader();
        saveOutboxEvents(events);

        log.info("Cleared RFID reader from tool {} and published events {}", toolId, events);
    }

    @Transactional
    public void addInstructors(@NotNull @Valid ToolId toolId, @NotEmpty List<@NotNull @Valid CustomerId> instructorIds) {
        Tool tool = this.toolRepository.findById(toolId.getValue()) //
                .orElseThrow(() -> ResourceNotFoundException.tool(toolId));

        List<SchaffbarEvent> events = tool.addInstructors(instructorIds);
        saveOutboxEvents(events);

        log.info("Added instructors {} to tool {} and published events {}", instructorIds, toolId, events);
    }

    @Transactional
    public void removeInstructors(@NotNull @Valid ToolId toolId, @NotEmpty List<@NotNull @Valid CustomerId> instructorIds) {
        Tool tool = this.toolRepository.findById(toolId.getValue()) //
                .orElseThrow(() -> ResourceNotFoundException.tool(toolId));

        List<SchaffbarEvent> events = tool.removeInstructors(instructorIds);
        saveOutboxEvents(events);

        log.info("Removed instructors {} from tool {} and published events {}", instructorIds, toolId, events);
    }

    @Transactional
    public void deleteTool(@NotNull @Valid ToolId id) {
        ToolView tool = getTool(id) //
                .orElseThrow(() -> ResourceNotFoundException.tool(id));

        this.toolRepository.deleteById(tool.id().getValue());

        List<SchaffbarEvent> events = List.of(ToolEventFactory.toolDeleted(id));
        saveOutboxEvents(events);

        log.info("Deleted tool with id {} and published events {}", id, events);
    }

    // ------------------------------------------------------------------------
    // helper

    // TODO: DRY: this method is duplicated in multiple services, maybe move to a common base class or utility class?
    private void saveOutboxEvents(List<SchaffbarEvent> events) {
        List<OutboxEvent> outboxEvents = events.stream() //
                .map(OutboxEvent::of) //
                .toList();

        this.outboxEventRepository.saveAll(outboxEvents);
    }

    private void throwToolNameAlreadyUsedException(String name, Tool existingTool) {
        throw new IllegalStateException("Tool name '" + name + "' is already used by tool " + existingTool.getId());
    }

    private void throwRfidReaderAlreadyAssignedException(RfidReaderId rfidReaderId, Tool existingTool) {
        throw new IllegalStateException("RFID reader " + rfidReaderId + " is already assigned to tool " + existingTool.getId());
    }

    private void throwIpAddressAlreadyUsedException(String ipAddress, Tool existingTool) {
        throw new IllegalStateException("IP address '" + ipAddress + "' is already used by tool " + existingTool.getId());
    }

}
