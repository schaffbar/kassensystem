package de.schaffbar.core_pos.shared.event.payload;

import de.schaffbar.core_pos.shared.id.RfidTagId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface RfidTagPayloads {

    record RfidTagCreatedPayload( //
            @Valid @NotNull RfidTagId id //
    ) implements RfidTagEventPayload {}

    record RfidTagDeletedPayload( //
            @Valid @NotNull RfidTagId id //
    ) implements RfidTagEventPayload {}

}
