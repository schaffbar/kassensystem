package de.schaffbar.core_pos.rfid_reader;

import de.schaffbar.core_pos.shared.event.rfid_reader.RfidReaderPayloads.RfidReaderCreatedPayload;
import de.schaffbar.core_pos.shared.event.rfid_reader.RfidReaderPayloads.RfidReaderUpdatedPayload;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface RfidReaderPayloadMapper {

    RfidReaderPayloadMapper MAPPER = Mappers.getMapper(RfidReaderPayloadMapper.class);

    // ------------------------------------------------------------------------
    // mapper

    RfidReaderCreatedPayload toRfidReaderCreatedPayload(RfidReader rfidReader);

    RfidReaderUpdatedPayload toRfidReaderUpdatedPayload(RfidReader rfidReader);

}
