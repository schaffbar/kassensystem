package de.schaffbar.core_pos.shared.event.payload;

import java.time.Instant;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.WorkshopSessionId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface WorkshopSessionPayloads {

    record WorkshopSessionStartedPayload( //
            @Valid @NotNull WorkshopSessionId id, //
            @Valid @NotNull CustomerId customerId, //
            @NotNull Instant startTime //
    ) implements WorkshopSessionEventPayload {}

    record WorkshopSessionClosedPayload( //
            @Valid @NotNull WorkshopSessionId id, //
            @Valid @NotNull CustomerId customerId, //
            @NotNull Instant startTime, //
            @NotNull Instant closeTime //
    ) implements WorkshopSessionEventPayload {}

}
