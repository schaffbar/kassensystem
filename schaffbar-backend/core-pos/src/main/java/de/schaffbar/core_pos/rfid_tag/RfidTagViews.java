package de.schaffbar.core_pos.rfid_tag;

import java.time.Instant;

import de.schaffbar.core_pos.id.RfidTagId;
import jakarta.validation.constraints.NotNull;

public interface RfidTagViews {

    record RfidTagView( //
            @NotNull RfidTagId id, //
            boolean active, //
            @NotNull Instant createdAt //
    ) {}

}
