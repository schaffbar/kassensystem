package de.schaffbar.core_pos.rfid_tag.web;

import de.schaffbar.core_pos.ValueObjectMapper;
import de.schaffbar.core_pos.rfid_tag.RfidTagCommands.CreateRfidTagCommand;
import de.schaffbar.core_pos.rfid_tag.RfidTagViews.RfidTagView;
import de.schaffbar.core_pos.rfid_tag.web.RfidTagApiModel.CreateRfidTagRequestBody;
import de.schaffbar.core_pos.rfid_tag.web.RfidTagApiModel.RfidTagApiDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RfidTagApiMapper extends ValueObjectMapper {

    RfidTagApiMapper MAPPER = Mappers.getMapper(RfidTagApiMapper.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "id", source = "id.value")
    RfidTagApiDto toRfidTagApiDto(RfidTagView rfidTag);

    // ------------------------------------------------------------------------
    // mapping request body to command

    CreateRfidTagCommand toCreateRfidTagCommand(CreateRfidTagRequestBody requestBody);

}
