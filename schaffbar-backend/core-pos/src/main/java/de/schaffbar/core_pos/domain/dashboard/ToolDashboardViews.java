package de.schaffbar.core_pos.domain.dashboard;

import de.schaffbar.core_pos.domain.customer.CustomerViews.CustomerView;
import de.schaffbar.core_pos.domain.tool.ToolViews.ToolView;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface ToolDashboardViews {

    // ------------------------------------------------------------------------
    // views

    record ToolDashboardEntryView( //
            @NotNull ToolId toolId, //
            @NotBlank String toolName, //
            @NotNull CustomerId customerId, //
            @NotBlank String firstName, //
            @NotBlank String lastName //
    ) {

        public static ToolDashboardEntryView of(@NotNull @Valid ToolView tool, @NotNull @Valid CustomerView customer) {
            return new ToolDashboardEntryView( //
                    tool.id(), //
                    tool.name(), //
                    customer.id(), //
                    customer.firstName(), //
                    customer.lastName() //
            );
        }

    }

}
