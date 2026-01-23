package de.schaffbar.core_pos.tool.web;

import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.tool.ToolViews.ToolView;
import jakarta.validation.constraints.NotBlank;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ToolApiModel {

    ToolApiModel MAPPER = Mappers.getMapper(ToolApiModel.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "rfidReaderId", source = "rfidReaderId.value")
    ToolApiDto toToolApiDto(ToolView tool);

    // ------------------------------------------------------------------------
    // mapping request body to command

    CreateToolCommand toCreateToolCommand(CreateToolRequestBody requestBody);

    // ------------------------------------------------------------------------
    // response

    record ToolApiDto( //
            @NotBlank String id, //
            @NotBlank String name, //
            String description, //
            String rfidReaderId //
    ) {}

    // ------------------------------------------------------------------------
    // request body

    record CreateToolRequestBody( //
            @NotBlank String name, //
            String description //
    ) {}

}
