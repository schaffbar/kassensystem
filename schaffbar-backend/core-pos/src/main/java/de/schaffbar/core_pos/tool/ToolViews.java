package de.schaffbar.core_pos.tool;

import de.schaffbar.core_pos.id.RfidReaderId;
import de.schaffbar.core_pos.id.ToolId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface ToolViews {

    record ToolView( //
            @NotNull ToolId id, //
            @NotBlank String name, //
            String description, //
            RfidReaderId rfidReaderId //
    ) {}

}
