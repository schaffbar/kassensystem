package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.domain.tool.ToolCommands.SetWlanRelaisCommand;
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
public class ToolSetWlanRelais {

    private final @NonNull ToolService toolService;

    @Transactional
    public void process(@NotNull @Valid ToolId toolId, @NotNull @Valid SetWlanRelaisCommand command) {
        this.toolService.setWlanRelais(toolId, command);
    }

}
