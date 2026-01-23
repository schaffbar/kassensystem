package de.schaffbar.core_pos.rfid_tag_assignment_history;

import java.time.Instant;

import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentType;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.RfidTagId;
import jakarta.validation.constraints.NotNull;

public interface RfidTagAssignmentHistoryViews {

    record RfidTagAssignmentHistoryView( //
            @NotNull CustomerId customerId, //
            @NotNull RfidTagId rfidTagId, //
            @NotNull RfidTagAssignmentType assignmentType, //
            @NotNull Instant assignmentDate, //
            @NotNull Instant unassignmentDate //
    ) {}

}
