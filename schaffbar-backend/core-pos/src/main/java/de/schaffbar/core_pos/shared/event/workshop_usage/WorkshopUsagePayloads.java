package de.schaffbar.core_pos.shared.event.workshop_usage;

import java.time.Instant;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.WorkshopSessionId;
import de.schaffbar.core_pos.shared.id.WorkshopUsageId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface WorkshopUsagePayloads {

    record WorkshopUsageEnteredPayload( //
            @Valid @NotNull WorkshopUsageId id, //
            @Valid @NotNull CustomerId customerId, //
            @Valid @NotNull WorkshopSessionId workshopSessionId, //
            @NotNull Instant entryTime //
    ) implements WorkshopUsageEventPayload {}

    record WorkshopUsageLeftPayload( //
            @Valid @NotNull WorkshopUsageId id, //
            @Valid @NotNull CustomerId customerId, //
            @Valid @NotNull WorkshopSessionId workshopSessionId, //
            @NotNull Instant entryTime, //
            @NotNull Instant exitTime //
    ) implements WorkshopUsageEventPayload {}

}
