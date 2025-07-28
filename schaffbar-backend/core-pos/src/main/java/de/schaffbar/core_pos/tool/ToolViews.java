package de.schaffbar.core_pos.tool;

import de.schaffbar.core_pos.ToolId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface ToolViews {

    record ToolView( //
            @NotNull ToolId id, //
            @NotBlank String name, //
            String description //
    ) {}

}
