package de.schaffbar.core_pos.rfid_tag;

import java.time.Instant;

import de.schaffbar.core_pos.RfidTagId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface RfidTagViews {

    record RfidTagView( //
            @NotNull RfidTagId id, //
            @NotBlank String tagId, //
            boolean active, //
            @NotNull Instant createdAt //
    ) {}

}
