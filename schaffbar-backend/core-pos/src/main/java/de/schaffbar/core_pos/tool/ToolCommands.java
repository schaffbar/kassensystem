package de.schaffbar.core_pos.tool;

import de.schaffbar.core_pos.shared.id.RfidReaderId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public interface ToolCommands {

    record CreateToolCommand( //
            @NotBlank String name,  //
            String description, //
            @Valid RfidReaderId rfidReaderId //
    ) {}

    record UpdateToolCommand( //
            @NotBlank String name,  //
            String description //
    ) {}

}
