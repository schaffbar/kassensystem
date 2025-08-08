package de.schaffbar.core_pos.rfid_reader;

import de.schaffbar.core_pos.rfid_reader.RfidReaderViews.RfidReaderView;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface RfidReaderViewMapper {

    RfidReaderViewMapper MAPPER = Mappers.getMapper(RfidReaderViewMapper.class);

    RfidReaderView toRfidReaderView(RfidReader rfidReader);

}
