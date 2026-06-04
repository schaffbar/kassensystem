package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.domain.tool.ToolCommands.UpdateToolCommand;
import de.schaffbar.core_pos.domain.tool.ToolService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class ToolUpdate {

    private final @NonNull ToolService toolService;

    @Transactional
    public void process(@NotNull @Valid ToolId toolId, @NotNull @Valid UpdateToolCommand command) {
        this.toolService.updateTool(toolId, command);
    }

}
