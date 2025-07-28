package de.schaffbar.core_pos.tool.web;

import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.tool.ToolViews.ToolView;
import de.schaffbar.core_pos.tool.web.ToolApiModel.CreateToolRequestBody;
import de.schaffbar.core_pos.tool.web.ToolApiModel.ToolApiDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ToolApiMapper {

    ToolApiMapper MAPPER = Mappers.getMapper(ToolApiMapper.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "id", source = "id.value")
    ToolApiDto toToolApiDto(ToolView tool);

    // ------------------------------------------------------------------------
    // mapping request body to command

    CreateToolCommand toCreateToolCommand(CreateToolRequestBody requestBody);

}
