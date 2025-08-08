package de.schaffbar.core_pos.rfid_tag_assignment;

import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentViews.RfidTagAssignmentView;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface RfidTagAssignmentViewMapper {

    RfidTagAssignmentViewMapper MAPPER = Mappers.getMapper(RfidTagAssignmentViewMapper.class);

    RfidTagAssignmentView toRfidTagAssignmentView(RfidTagAssignment rfidTagAssignment);

}
