package de.schaffbar.core_pos.workshop_dashboard;

import java.time.Instant;

import de.schaffbar.core_pos.shared.id.CustomerId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public interface WorkshopDashboardViews {

    // ------------------------------------------------------------------------
    // views

    record WorkshopDashboardEntryView( //
            @NotNull CustomerId customerId, //
            @NotBlank String firstName, //
            @NotBlank String lastName, //
            @NotNull @PastOrPresent Instant entryTime //
    ) {}

}
