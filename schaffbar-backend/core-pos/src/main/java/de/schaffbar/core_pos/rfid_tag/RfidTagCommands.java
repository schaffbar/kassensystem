package de.schaffbar.core_pos.rfid_tag;

import jakarta.validation.constraints.NotBlank;

public interface RfidTagCommands {

    record CreateRfidTagCommand( //
            @NotBlank String tagId  //
    ) {}

}
