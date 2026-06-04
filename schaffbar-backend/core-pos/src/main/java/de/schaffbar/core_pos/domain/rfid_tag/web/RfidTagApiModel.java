package de.schaffbar.core_pos.domain.rfid_tag.web;

import de.schaffbar.core_pos.domain.rfid_tag.RfidTagCommands.CreateRfidTagCommand;
import de.schaffbar.core_pos.domain.rfid_tag.RfidTagViews.RfidTagView;
import de.schaffbar.core_pos.shared.id.ValueObjectMapper;
import jakarta.validation.constraints.NotBlank;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RfidTagApiModel extends ValueObjectMapper {

    RfidTagApiModel MAPPER = Mappers.getMapper(RfidTagApiModel.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "id", source = "id.value")
    RfidTagApiDto toRfidTagApiDto(RfidTagView rfidTag);

    // ------------------------------------------------------------------------
    // mapping request body to command

    CreateRfidTagCommand toCreateRfidTagCommand(CreateRfidTagRequestBody requestBody);

    // ------------------------------------------------------------------------
    // response

    record RfidTagApiDto( //
            @NotBlank String id, //
            boolean active //
    ) {}

    // ------------------------------------------------------------------------
    // request body

    record CreateRfidTagRequestBody( //
            @NotBlank String rfidTagId //
    ) {}

}
