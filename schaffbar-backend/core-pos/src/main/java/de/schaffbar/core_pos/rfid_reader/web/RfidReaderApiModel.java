package de.schaffbar.core_pos.rfid_reader.web;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface RfidReaderApiModel {

    // ------------------------------------------------------------------------
    // response

    record RfidReaderApiDto( //
            @NotBlank String id, //
            @NotBlank String macAddress, //
            String type, //
            @NotNull Instant createdAt //
    ) {}

    // ------------------------------------------------------------------------
    // request body

    record CreateRfidReaderRequestBody( //
            @NotBlank String macAddress  //
    ) {}

}
