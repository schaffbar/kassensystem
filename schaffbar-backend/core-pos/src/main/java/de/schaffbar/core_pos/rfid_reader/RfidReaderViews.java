package de.schaffbar.core_pos.rfid_reader;

import java.time.Instant;

import de.schaffbar.core_pos.RfidReaderId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface RfidReaderViews {

    record RfidReaderView( //
            @NotNull RfidReaderId id, //
            @NotBlank String macAddress, //
            RfidReaderType type, //
            @NotNull Instant createdAt //
    ) {}

}
