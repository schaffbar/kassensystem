package de.schaffbar.core_pos.rfid_tag_assignment;

import java.time.Instant;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.RfidTagAssignmentId;
import de.schaffbar.core_pos.RfidTagId;
import jakarta.validation.constraints.NotNull;

public interface RfidTagAssignmentViews {

    record RfidTagAssignmentView( //
            @NotNull RfidTagAssignmentId id, //
            @NotNull CustomerId customerId, //
            @NotNull RfidTagAssignmentType assignmentType, //
            @NotNull RfidTagAssignmentStatus status, //
            RfidTagId rfidTagId, //
            Instant assignmentDate //
    ) {}

}
