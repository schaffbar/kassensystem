package de.schaffbar.core_pos.rfid_tag;

import jakarta.validation.constraints.NotBlank;

public interface RfidTagCommands {

    record CreateRfidTagCommand( //
            // TODO: switch to RfidTagId value object
            @NotBlank String rfidTagId  //
    ) {}

}
