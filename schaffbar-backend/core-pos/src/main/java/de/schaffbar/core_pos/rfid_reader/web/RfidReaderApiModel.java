package de.schaffbar.core_pos.rfid_reader.web;

import jakarta.validation.constraints.NotBlank;

public interface RfidReaderApiModel {

    // ------------------------------------------------------------------------
    // response

    record RfidReaderApiDto( //
            @NotBlank String id, //
            @NotBlank String macAddress) {}

    // ------------------------------------------------------------------------
    // request body

    record CreateRfidReaderRequestBody( //
            @NotBlank String macAddress //
    ) {}

}
