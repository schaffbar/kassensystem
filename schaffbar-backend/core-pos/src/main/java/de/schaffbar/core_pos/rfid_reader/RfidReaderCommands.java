package de.schaffbar.core_pos.rfid_reader;

import de.schaffbar.core_pos.MacAddress;
import jakarta.validation.constraints.NotNull;

public interface RfidReaderCommands {

    record CreateRfidReaderCommand( //
            @NotNull MacAddress macAddress //
    ) {}

}
