package de.schaffbar.core_pos.workshop_usage;

import java.time.Duration;
import java.time.Instant;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.WorkshopSessionId;
import de.schaffbar.core_pos.WorkshopUsageId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public interface WorkshopUsageViews {

    record WorkshopUsageView( //
            @NotNull WorkshopUsageId id, //
            @NotNull CustomerId customerId, //
            @NotNull WorkshopSessionId workshopSessionId, //
            @NotNull @PastOrPresent Instant entryTime, //
            @PastOrPresent Instant exitTime, //
            Duration duration //
    ) {}

}
