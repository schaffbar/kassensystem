package de.schaffbar.core_pos.rfid_reader;

import de.schaffbar.core_pos.shared.id.RfidReaderId;
import jakarta.validation.constraints.NotNull;

public interface RfidReaderCommands {

    record UpdateRfidReaderCommand( //
            @NotNull RfidReaderId id, //
            String name, //
            String socketName //
    ) {}

    record ChangeRfidReaderTypeCommand( //
            @NotNull RfidReaderId id, //
            @NotNull RfidReaderType type //
    ) {}

}
