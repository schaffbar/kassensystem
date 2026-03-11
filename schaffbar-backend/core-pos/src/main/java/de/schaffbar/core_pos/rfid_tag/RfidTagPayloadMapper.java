package de.schaffbar.core_pos.rfid_tag;

import de.schaffbar.core_pos.shared.event.rfid_tag.RfidTagPayloads.RfidTagCreatedPayload;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface RfidTagPayloadMapper {

    RfidTagPayloadMapper MAPPER = Mappers.getMapper(RfidTagPayloadMapper.class);

    // ------------------------------------------------------------------------
    // mapper

    RfidTagCreatedPayload toRfidTagCreatedPayload(RfidTag rfidTag);

}
