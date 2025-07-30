package de.schaffbar.core_pos.rfid_reader.web;

import de.schaffbar.core_pos.ValueObjectMapper;
import de.schaffbar.core_pos.rfid_reader.RfidReaderCommands.CreateRfidReaderCommand;
import de.schaffbar.core_pos.rfid_reader.RfidReaderViews.RfidReaderView;
import de.schaffbar.core_pos.rfid_reader.web.RfidReaderApiModel.CreateRfidReaderRequestBody;
import de.schaffbar.core_pos.rfid_reader.web.RfidReaderApiModel.RfidReaderApiDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RfidReaderApiMapper extends ValueObjectMapper {

    RfidReaderApiMapper MAPPER = Mappers.getMapper(RfidReaderApiMapper.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "id", source = "id.value")
    RfidReaderApiDto toRfidReaderApiDto(RfidReaderView rfidReader);

    // ------------------------------------------------------------------------
    // mapping request body to command

    CreateRfidReaderCommand toCreateRfidReaderCommand(CreateRfidReaderRequestBody requestBody);

}
