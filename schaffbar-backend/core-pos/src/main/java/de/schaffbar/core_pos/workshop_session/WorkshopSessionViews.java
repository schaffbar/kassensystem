package de.schaffbar.core_pos.workshop_session;

import java.time.Instant;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.WorkshopSessionId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public interface WorkshopSessionViews {

    record WorkshopSessionView( //
            @NotNull WorkshopSessionId id, //
            @NotNull CustomerId customerId, //
            @NotNull @PastOrPresent Instant startTime, //
            @PastOrPresent Instant closeTime, //
            @NotNull WorkshopSessionStatus status //
    ) {}

}
