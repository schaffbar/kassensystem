package de.schaffbar.core_pos.rfid_tag_assignment_history.web;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentType;
import jakarta.validation.constraints.NotNull;

public interface RfidTagAssignmentHistoryApiModel {

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
