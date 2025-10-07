package de.schaffbar.core_pos.rfid_tag_assignment_history;

import de.schaffbar.core_pos.rfid_tag_assignment_history.RfidTagAssignmentHistoryViews.RfidTagAssignmentHistoryView;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface RfidTagAssignmentHistoryViewMapper {

    RfidTagAssignmentHistoryViewMapper MAPPER = Mappers.getMapper(RfidTagAssignmentHistoryViewMapper.class);

    RfidTagAssignmentHistoryView toRfidTagAssignmentView(RfidTagAssignmentHistory rfidTagAssignment);

}
