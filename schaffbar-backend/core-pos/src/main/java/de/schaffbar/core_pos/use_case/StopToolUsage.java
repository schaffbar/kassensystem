package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool_usage.ToolUsageService;
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
public class StopToolUsage {

    private final @NonNull ToolUsageService toolUsageService;

    @Transactional
    public void process(@NotNull @Valid CustomerId customerId, @NotNull @Valid ToolId toolId) {
        this.toolUsageService.stopUsage(customerId, toolId);
    }

}
