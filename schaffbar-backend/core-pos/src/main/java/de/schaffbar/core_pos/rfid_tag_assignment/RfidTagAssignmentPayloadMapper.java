package de.schaffbar.core_pos.rfid_tag_assignment;

import de.schaffbar.core_pos.shared.event.rfid_tag_assignment.RfidTagAssignmentPayloads.RfidTagAssignedPayload;
import de.schaffbar.core_pos.shared.event.rfid_tag_assignment.RfidTagAssignmentPayloads.RfidTagAssignmentRequestedPayload;
import de.schaffbar.core_pos.shared.event.rfid_tag_assignment.RfidTagAssignmentPayloads.RfidTagUnassignedPayload;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface RfidTagAssignmentPayloadMapper {

    RfidTagAssignmentPayloadMapper MAPPER = Mappers.getMapper(RfidTagAssignmentPayloadMapper.class);

    // ------------------------------------------------------------------------
    // mapper

    RfidTagAssignmentRequestedPayload toRfidTagAssignmentRequestedPayload(RfidTagAssignment assignment);

    RfidTagAssignedPayload toRfidTagAssignedPayload(RfidTagAssignment assignment);

    RfidTagUnassignedPayload toRfidTagUnassignedPayload(RfidTagAssignment assignment);

}
