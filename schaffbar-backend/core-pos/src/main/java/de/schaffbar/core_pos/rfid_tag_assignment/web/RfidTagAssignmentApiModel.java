package de.schaffbar.core_pos.rfid_tag_assignment.web;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentStatus;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentType;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentViews.RfidTagAssignmentView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RfidTagAssignmentApiModel {

    RfidTagAssignmentApiModel MAPPER = Mappers.getMapper(RfidTagAssignmentApiModel.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "customerId", source = "customerId.value")
    @Mapping(target = "rfidTagId", source = "rfidTagId.value")
    RfidTagAssignmentApiDto toRfidTagAssignmentApiDto(RfidTagAssignmentView rfidTagAssignment);

    // ------------------------------------------------------------------------
    // mapping request body to command

    // ------------------------------------------------------------------------
    // response

    record RfidTagAssignmentApiDto( //
            @NotNull UUID id, //
            @NotNull UUID customerId, //
            @NotNull RfidTagAssignmentType assignmentType, //
            @NotNull RfidTagAssignmentStatus status, //
            String rfidTagId, //
            Instant assignmentDate //
    ) {}

    // ------------------------------------------------------------------------
    // request body

    record RequestRfidTagAssignmentRequestBody( //
            @NotNull UUID customerId, //
            @NotNull RfidTagAssignmentType assignmentType //
    ) {}

    record AssignRfidTagRequestBody( //
            @NotBlank String rfidTagId //
    ) {}

    record UnassignRfidTagRequestBody( //
            @NotNull UUID customerId //
    ) {}

}
