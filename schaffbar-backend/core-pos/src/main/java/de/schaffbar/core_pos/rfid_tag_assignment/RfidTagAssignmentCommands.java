package de.schaffbar.core_pos.rfid_tag_assignment;

import de.schaffbar.core_pos.CustomerId;
import jakarta.validation.constraints.NotNull;

public interface RfidTagAssignmentCommands {

    record RequestRfidTagAssignmentCommand( //
            @NotNull CustomerId customerId, //
            @NotNull RfidTagAssignmentType assignmentType //
    ) {}

}
