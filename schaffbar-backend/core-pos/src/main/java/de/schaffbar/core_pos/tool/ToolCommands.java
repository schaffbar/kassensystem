package de.schaffbar.core_pos.tool;

import jakarta.validation.constraints.NotBlank;

public interface ToolCommands {

    record CreateToolCommand( //
            @NotBlank String name,  //
            String description //
    ) {}

    record UpdateToolCommand( //
            @NotBlank String name,  //
            String description //
    ) {}

}
