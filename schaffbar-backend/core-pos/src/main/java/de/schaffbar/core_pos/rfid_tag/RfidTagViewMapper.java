package de.schaffbar.core_pos.rfid_tag;

import de.schaffbar.core_pos.rfid_tag.RfidTagViews.RfidTagView;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RfidTagViewMapper {

    RfidTagViewMapper MAPPER = Mappers.getMapper(RfidTagViewMapper.class);

    RfidTagView toRfidTagView(RfidTag rfidTag);

}
