package de.schaffbar.core_pos.tool;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateWlanRelaisCommand;
import de.schaffbar.core_pos.tool.ToolViews.ToolView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class ToolService {

    private final @NonNull ToolRepository toolRepository;

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

        return savedTool.getId();
    }

    @Transactional
    public void updateTool(@NotNull @Valid ToolId toolId, @NotNull @Valid UpdateToolCommand command) {
        Tool tool = this.toolRepository.findById(toolId.getValue()) //
                .orElseThrow(() -> ResourceNotFoundException.tool(toolId));

        tool.update(command);
    }

    @Transactional
    public void updateWlanRelais(@NotNull @Valid ToolId toolId, @NotNull @Valid UpdateWlanRelaisCommand command) {
        Tool tool = this.toolRepository.findById(toolId.getValue()) //
                .orElseThrow(() -> ResourceNotFoundException.tool(toolId));

        if (nonNull(command.ipAddress())) {
            this.toolRepository.findByIpAddress(command.ipAddress()) //
                    .filter(existingTool -> isFalse(toolId.sameValueAs(existingTool.getId()))) //
                    .ifPresent(existingTool -> throwIpAddressAlreadyUsedException(command.ipAddress(), existingTool));
        }

        tool.updateWlanRelais(command);
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

        tool.assignRfidReader(rfidReaderId);
    }

    @Transactional
    public void clearRfidReader(@NotNull @Valid ToolId toolId) {
        Tool tool = this.toolRepository.findById(toolId.getValue()) //
                .orElseThrow(() -> ResourceNotFoundException.tool(toolId));

        tool.clearRfidReader();
    }

    @Transactional
    public void deleteTool(@NotNull @Valid ToolId id) {
        ToolView tool = getTool(id) //
                .orElseThrow(() -> ResourceNotFoundException.tool(id));

        this.toolRepository.deleteById(tool.id().getValue());
    }

    // ------------------------------------------------------------------------
    // helper

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
