package de.schaffbar.core_pos.tool.web;

import jakarta.validation.constraints.NotBlank;

public interface ToolApiModel {

    // ------------------------------------------------------------------------
    // response

    record ToolApiDto( //
            @NotBlank String id, //
            @NotBlank String name, //
            String description //
    ) {}

    // ------------------------------------------------------------------------
    // request body

    record CreateToolRequestBody( //
            @NotBlank String name, //
            String description //
    ) {}

}
