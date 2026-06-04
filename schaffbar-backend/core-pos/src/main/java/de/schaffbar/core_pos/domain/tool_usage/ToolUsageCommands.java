package de.schaffbar.core_pos.domain.tool_usage;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.shared.id.WorkshopSessionId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface ToolUsageCommands {

    record StartToolUsageCommand( //
            @Valid @NotNull CustomerId customerId, //
            @Valid @NotNull ToolId toolId, //
            @Valid @NotNull WorkshopSessionId workshopSessionId //
    ) {}

}
