package de.schaffbar.core_pos.rfid_reader;

import jakarta.validation.constraints.NotBlank;

public interface RfidReaderCommands {

    record CreateRfidReaderCommand( //
            @NotBlank String macAddress //
    ) {}

}
