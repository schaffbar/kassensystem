package de.schaffbar.core_pos.tool;

import static org.apache.commons.lang3.BooleanUtils.isFalse;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEventRepository;
import de.schaffbar.core_pos.shared.exception.IpAddressAlreadyUsedException;
import de.schaffbar.core_pos.shared.exception.RfidReaderAlreadyAssignedToToolException;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.exception.ToolNameAlreadyUsedException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.IpAddress;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.SetWlanRelaisCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateToolCommand;
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
        Tool tool = findOrThrow(toolId);

        return tool.isInstructor(customerId);
    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public ToolId createTool(@NotNull @Valid CreateToolCommand command) {
        this.toolRepository.findByName(command.name()) //
                .ifPresent(existing -> throwToolNameAlreadyUsedException(command.name(), existing));

        Tool tool = Tool.of(command);
        Tool savedTool = this.toolRepository.save(tool);

        List<SchaffbarEvent> events = List.of(ToolEventFactory.toolCreated(savedTool));
        saveOutboxEvents(events);

        log.info("Created tool with id {} and published events {}", savedTool.getId(), events);

        return savedTool.getId();
    }

    @Transactional
    public void updateTool(@NotNull @Valid ToolId toolId, @NotNull @Valid UpdateToolCommand command) {
        Tool tool = findOrThrow(toolId);

        this.toolRepository.findByName(command.name()) //
                .filter(existing -> isFalse(toolId.sameValueAs(existing.getId()))) //
                .ifPresent(existing -> throwToolNameAlreadyUsedException(command.name(), existing));

        List<SchaffbarEvent> events = tool.update(command);
        saveOutboxEvents(events);

        log.info("Updated tool with id {} and published events {}", toolId, events);
    }

    @Transactional
    public void setWlanRelais(@NotNull @Valid ToolId toolId, @NotNull @Valid SetWlanRelaisCommand command) {
        Tool tool = findOrThrow(toolId);

        this.toolRepository.findByIpAddress(command.ipAddress().getValue()) //
                .filter(existing -> isFalse(toolId.sameValueAs(existing.getId()))) //
                .ifPresent(existing -> throwIpAddressAlreadyUsedException(command.ipAddress(), existing));

        List<SchaffbarEvent> events = tool.setWlanRelais(command);
        saveOutboxEvents(events);

        log.info("Set WLAN relais of tool with id {} and published events {}", toolId, events);
    }

    @Transactional
    public void clearWlanRelais(@NotNull @Valid ToolId toolId) {
        Tool tool = findOrThrow(toolId);

        List<SchaffbarEvent> events = tool.clearWlanRelais();
        saveOutboxEvents(events);

        log.info("Cleared WLAN relais of tool with id {} and published events {}", toolId, events);
    }

    @Transactional
    public void assignRfidReader(@NotNull @Valid ToolId toolId, @NotNull @Valid RfidReaderId rfidReaderId) {
        Tool tool = findOrThrow(toolId);

        if (rfidReaderId.sameValueAs(tool.getRfidReaderId())) {
            return;
        }

        this.toolRepository.findByRfidReaderId(rfidReaderId) //
                .ifPresent(existing -> throwRfidReaderAlreadyAssignedException(rfidReaderId, existing));

        List<SchaffbarEvent> events = tool.assignRfidReader(rfidReaderId);
        saveOutboxEvents(events);

        log.info("Assigned RFID reader {} to tool {} and published events {}", rfidReaderId, toolId, events);
    }

    @Transactional
    public void clearRfidReader(@NotNull @Valid ToolId toolId) {
        Tool tool = findOrThrow(toolId);

        List<SchaffbarEvent> events = tool.clearRfidReader();
        saveOutboxEvents(events);

        log.info("Cleared RFID reader from tool {} and published events {}", toolId, events);
    }

    @Transactional
    public void addInstructors(@NotNull @Valid ToolId toolId, @NotEmpty List<@NotNull @Valid CustomerId> instructorIds) {
        Tool tool = findOrThrow(toolId);

        List<SchaffbarEvent> events = tool.addInstructors(instructorIds);
        saveOutboxEvents(events);

        log.info("Added instructors {} to tool {} and published events {}", instructorIds, toolId, events);
    }

    @Transactional
    public void removeInstructors(@NotNull @Valid ToolId toolId, @NotEmpty List<@NotNull @Valid CustomerId> instructorIds) {
        Tool tool = findOrThrow(toolId);

        List<SchaffbarEvent> events = tool.removeInstructors(instructorIds);
        saveOutboxEvents(events);

        log.info("Removed instructors {} from tool {} and published events {}", instructorIds, toolId, events);
    }

    @Transactional
    public void deleteTool(@NotNull @Valid ToolId toolId) {
        findOrThrow(toolId);

        this.toolRepository.deleteById(toolId.getValue());

        List<SchaffbarEvent> events = List.of(ToolEventFactory.toolDeleted(toolId));
        saveOutboxEvents(events);

        log.info("Deleted tool with id {} and published events {}", toolId, events);
    }

    // ------------------------------------------------------------------------
    // helper

    private Tool findOrThrow(ToolId toolId) {
        return this.toolRepository.findById(toolId.getValue()) //
                .orElseThrow(() -> ResourceNotFoundException.tool(toolId));
    }

    private void saveOutboxEvents(List<SchaffbarEvent> events) {
        List<OutboxEvent> outboxEvents = events.stream() //
                .map(OutboxEvent::of) //
                .toList();

        this.outboxEventRepository.saveAll(outboxEvents);
    }

    private void throwToolNameAlreadyUsedException(String name, Tool existingTool) {
        throw new ToolNameAlreadyUsedException(name, existingTool.getId());
    }

    private void throwIpAddressAlreadyUsedException(IpAddress ipAddress, Tool existingTool) {
        throw new IpAddressAlreadyUsedException(ipAddress, existingTool.getId());
    }

    private void throwRfidReaderAlreadyAssignedException(RfidReaderId rfidReaderId, Tool existingTool) {
        throw new RfidReaderAlreadyAssignedToToolException(rfidReaderId, existingTool.getId());
    }

}
