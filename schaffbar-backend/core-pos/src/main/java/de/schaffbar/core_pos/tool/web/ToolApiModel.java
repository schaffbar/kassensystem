package de.schaffbar.core_pos.tool.web;

import java.util.List;
import java.util.Set;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ValueObjectMapper;
import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateWlanRelaisCommand;
import de.schaffbar.core_pos.tool.ToolViews.ToolView;
import de.schaffbar.core_pos.tool.WlanRelaisType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @Mapping(target = "instructorIds", expression = "java(mapInstructorIds(tool.instructors()))")
    ToolApiDto toToolApiDto(ToolView tool);

    default List<String> mapInstructorIds(Set<CustomerId> instructors) {
        if (instructors == null) {
            return List.of();
        }

        return instructors.stream() //
                .map(id -> id.getValue().toString()) //
                .toList();
    }

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
            WlanRelaisType wlanRelaisType, //
            String ipAddress, //
            String httpStartCommand, //
            String onCommand, //
            String offCommand, //
            List<String> instructorIds //
    ) {}

    // ------------------------------------------------------------------------
    // request body

    record CreateToolRequestBody( //
            @NotBlank String name, //
            String description, //
            String rfidReaderId, // TODO: remove it
            WlanRelaisType wlanRelaisType, // TODO: remove it
            String ipAddress // TODO: remove it
    ) {}

    record UpdateToolRequestBody( //
            String name, //
            String description //
    ) {}

    record UpdateWlanRelaisRequestBody( //
            WlanRelaisType wlanRelaisType, //
            String ipAddress // TODO: ip address value object with validation
    ) {}

    record InstructorsRequestBody( //
            @NotEmpty List<@NotNull CustomerId> instructorIds //
    ) {}

}
