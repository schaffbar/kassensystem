package de.schaffbar.core_pos.domain.tool.web;

import java.util.List;
import java.util.Set;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.IpAddress;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.shared.id.ValueObjectMapper;
import de.schaffbar.core_pos.domain.tool.CertificationRequirement;
import de.schaffbar.core_pos.domain.tool.ToolArea;
import de.schaffbar.core_pos.domain.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.domain.tool.ToolCommands.SetWlanRelaisCommand;
import de.schaffbar.core_pos.domain.tool.ToolCommands.UpdateToolCommand;
import de.schaffbar.core_pos.domain.tool.ToolViews.ToolView;
import de.schaffbar.core_pos.domain.tool.WlanRelaisType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ToolApiModel extends ValueObjectMapper {

    ToolApiModel MAPPER = Mappers.getMapper(ToolApiModel.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    ToolApiDto toToolApiDto(ToolView tool);

    // ------------------------------------------------------------------------
    // mapping request body to command

    CreateToolCommand toCreateToolCommand(CreateToolRequestBody requestBody);

    UpdateToolCommand toUpdateToolCommand(UpdateToolRequestBody requestBody);

    SetWlanRelaisCommand toSetWlanRelaisCommand(SetWlanRelaisRequestBody requestBody);

    // ------------------------------------------------------------------------
    // response

    record ToolApiDto( //
            @NotNull ToolId id, //
            @NotBlank String name, //
            String description, //
            @NotNull ToolArea area, //
            @NotNull CertificationRequirement certificationRequirement, //
            RfidReaderId rfidReaderId, //
            WlanRelaisType wlanRelaisType, //
            IpAddress ipAddress, //
            String httpStartCommand, //
            String onCommand, //
            String offCommand, //
            Set<CustomerId> instructors //
    ) {}

    // ------------------------------------------------------------------------
    // request body

    record CreateToolRequestBody( //
            @NotBlank String name, //
            String description, //
            @NotNull ToolArea area, //
            @NotNull CertificationRequirement certificationRequirement //
    ) {}

    record UpdateToolRequestBody( //
            @NotBlank String name, //
            String description, //
            @NotNull ToolArea area, //
            @NotNull CertificationRequirement certificationRequirement //
    ) {}

    record SetWlanRelaisRequestBody( //
            @NotNull WlanRelaisType wlanRelaisType, //
            @NotNull @Valid IpAddress ipAddress //
    ) {}

    record InstructorsRequestBody( //
            @NotEmpty List<@NotNull CustomerId> instructorIds //
    ) {}

}
