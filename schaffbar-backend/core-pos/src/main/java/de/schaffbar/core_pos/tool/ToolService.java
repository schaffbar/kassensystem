package de.schaffbar.core_pos.tool;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.ToolId;
import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.tool.ToolViews.ToolView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
                .map(ToolViewMapper.MAPPER::toToolView) //
                .toList();
    }

    public Optional<ToolView> getTool(@NotNull @Valid ToolId toolId) {
        return this.toolRepository.findById(toolId.getValue()) //
                .map(ToolViewMapper.MAPPER::toToolView);
    }
    
    // ------------------------------------------------------------------------
    // command

    public ToolId createTool(@NotNull @Valid CreateToolCommand command) {
        Tool tool = Tool.of(command);
        Tool savedTool = this.toolRepository.save(tool);

        return savedTool.getId();
    }

    public void deleteTool(@NotNull @Valid ToolId id) {
        ToolView toolView = getTool(id) //
                .orElseThrow(() -> ResourceNotFoundException.tool(id));

        this.toolRepository.deleteById(toolView.id().getValue());
    }

}
