package de.schaffbar.core_pos.rfid_tag.web;

import jakarta.validation.constraints.NotBlank;

public interface RfidTagApiModel {

    // ------------------------------------------------------------------------
    // response

    record RfidTagApiDto( //
            @NotBlank String id, //
            @NotBlank String tagId, //
            boolean active //
    ) {}

    // ------------------------------------------------------------------------
    // request body

    record CreateRfidTagRequestBody( //
            @NotBlank String tagId //
    ) {}

}
