package de.schaffbar.core_pos.rfid_tag_assignment.web;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentStatus;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface RfidTagAssignmentApiModel {

    // ------------------------------------------------------------------------
    // response

    record RfidTagAssignmentApiDto( //
            @NotNull UUID id, //
            @NotNull UUID customerId, //
            @NotNull RfidTagAssignmentType assignmentType, //
            String rfidTagId, //
            Instant assignmentDate, //
            Instant unassignmentDate, //
            @NotNull RfidTagAssignmentStatus status //
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

}
