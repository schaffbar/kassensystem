package de.schaffbar.core_pos.tool;

import de.schaffbar.core_pos.shared.id.IpAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface ToolCommands {

    record CreateToolCommand( //
            @NotBlank String name, //
            String description //
    ) {}

    record UpdateToolCommand( //
            @NotBlank String name, //
            String description //
    ) {}

    record SetWlanRelaisCommand( //
            @NotNull WlanRelaisType wlanRelaisType, //
            @NotNull @Valid IpAddress ipAddress //
    ) {}

}
