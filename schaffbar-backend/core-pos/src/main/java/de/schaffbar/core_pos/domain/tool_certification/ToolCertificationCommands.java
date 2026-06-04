package de.schaffbar.core_pos.domain.tool_certification;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface ToolCertificationCommands {

    record CreateToolCertificationCommand( //
            @Valid @NotNull CustomerId customerId, //
            @Valid @NotNull ToolId toolId, //
            @Valid CustomerId certifiedBy //
    ) {}

}
