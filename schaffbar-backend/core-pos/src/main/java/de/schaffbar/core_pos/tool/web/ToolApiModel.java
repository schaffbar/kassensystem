package de.schaffbar.core_pos.tool.web;

import de.schaffbar.core_pos.shared.id.ValueObjectMapper;
import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateWlanRelaisCommand;
import de.schaffbar.core_pos.tool.ToolViews.ToolView;
import de.schaffbar.core_pos.tool.WlanRelaisType;
import jakarta.validation.constraints.NotBlank;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ToolApiModel extends ValueObjectMapper {

    ToolApiModel MAPPER = Mappers.getMapper(ToolApiModel.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "rfidReaderId", source = "rfidReaderId.value")
    ToolApiDto toToolApiDto(ToolView tool);

    // ------------------------------------------------------------------------
    // mapping request body to command

    CreateToolCommand toCreateToolCommand(CreateToolRequestBody requestBody);

    UpdateToolCommand toUpdateToolCommand(UpdateToolRequestBody requestBody);

    UpdateWlanRelaisCommand toUpdateWlanRelaisCommand(UpdateWlanRelaisRequestBody requestBody);

    // ------------------------------------------------------------------------
    // response

    record ToolApiDto( //
            @NotBlank String id, //
            @NotBlank String name, //
            String description, //
            String rfidReaderId, //
            String ipAddress, //
            String httpStartCommand, //
            String onCommand, //
            String offCommand //
    ) {}

    // ------------------------------------------------------------------------
    // request body

    record CreateToolRequestBody( //
            @NotBlank String name, //
            String description, //
            String rfidReaderId, //
            WlanRelaisType wlanRelaisType, //
            String ipAddress //
    ) {}

    record UpdateToolRequestBody( //
            String name, //
            String description //
    ) {}

    record UpdateWlanRelaisRequestBody( //
            WlanRelaisType wlanRelaisType, //
            String ipAddress //
    ) {}

}
