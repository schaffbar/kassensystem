package de.schaffbar.core_pos.rfid_tag_assignment.web;

import de.schaffbar.core_pos.ValueObjectMapper;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentCommands.RequestRfidTagAssignmentCommand;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentViews.RfidTagAssignmentView;
import de.schaffbar.core_pos.rfid_tag_assignment.web.RfidTagAssignmentApiModel.RequestRfidTagAssignmentRequestBody;
import de.schaffbar.core_pos.rfid_tag_assignment.web.RfidTagAssignmentApiModel.RfidTagAssignmentApiDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RfidTagAssignmentApiMapper extends ValueObjectMapper {

    RfidTagAssignmentApiMapper MAPPER = Mappers.getMapper(RfidTagAssignmentApiMapper.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "customerId", source = "customerId.value")
    @Mapping(target = "rfidTagId", source = "rfidTagId.value")
    RfidTagAssignmentApiDto toRfidTagAssignmentApiDto(RfidTagAssignmentView rfidTagAssignment);

    // ------------------------------------------------------------------------
    // mapping request body to command

    RequestRfidTagAssignmentCommand toRequestRfidTagAssignmentCommand(RequestRfidTagAssignmentRequestBody requestBody);

}
