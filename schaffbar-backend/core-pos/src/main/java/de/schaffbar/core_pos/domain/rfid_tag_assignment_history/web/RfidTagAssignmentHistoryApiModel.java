package de.schaffbar.core_pos.domain.rfid_tag_assignment_history.web;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.domain.rfid_tag_assignment.RfidTagAssignmentType;
import de.schaffbar.core_pos.domain.rfid_tag_assignment_history.RfidTagAssignmentHistoryViews.RfidTagAssignmentHistoryView;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RfidTagAssignmentHistoryApiModel {

    RfidTagAssignmentHistoryApiModel MAPPER = Mappers.getMapper(RfidTagAssignmentHistoryApiModel.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "customerId", source = "customerId.value")
    @Mapping(target = "rfidTagId", source = "rfidTagId.value")
    RfidTagAssignmentHistoryApiDto toRfidTagAssignmentHistoryApiDto(RfidTagAssignmentHistoryView rfidTagAssignment);

    // ------------------------------------------------------------------------
    // mapping request body to command

    // ------------------------------------------------------------------------
    // response

    record RfidTagAssignmentHistoryApiDto( //
            @NotNull UUID customerId, //
            @NotNull String rfidTagId, //
            @NotNull RfidTagAssignmentType assignmentType, //
            @NotNull Instant assignmentDate, //
            @NotNull Instant unassignmentDate //
    ) {}

    // ------------------------------------------------------------------------
    // request body

}
