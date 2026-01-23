package de.schaffbar.core_pos.rfid_tag_assignment_history.web;

import de.schaffbar.core_pos.rfid_tag_assignment_history.RfidTagAssignmentHistoryViews.RfidTagAssignmentHistoryView;
import de.schaffbar.core_pos.rfid_tag_assignment_history.web.RfidTagAssignmentHistoryApiModel.RfidTagAssignmentHistoryApiDto;
import de.schaffbar.core_pos.shared.id.ValueObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RfidTagAssignmentHistoryApiMapper extends ValueObjectMapper {

    RfidTagAssignmentHistoryApiMapper MAPPER = Mappers.getMapper(RfidTagAssignmentHistoryApiMapper.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "customerId", source = "customerId.value")
    @Mapping(target = "rfidTagId", source = "rfidTagId.value")
    RfidTagAssignmentHistoryApiDto toRfidTagAssignmentHistoryApiDto(RfidTagAssignmentHistoryView rfidTagAssignment);

    // ------------------------------------------------------------------------
    // mapping request body to command

}
