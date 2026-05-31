package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool.ToolService;
import de.schaffbar.core_pos.tool_certification.ToolCertificationService;
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
public class ToolDelete {

    private final @NonNull ToolService toolService;

    private final @NonNull ToolCertificationService toolCertificationService;

    @Transactional
    public void process(@NotNull @Valid ToolId toolId) {
        this.toolCertificationService.deleteAllByToolId(toolId);
        this.toolService.deleteTool(toolId);
    }

}
