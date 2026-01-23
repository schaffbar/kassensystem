package de.schaffbar.core_pos.rfid_tag_assignment_history;

import java.time.Instant;

import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentType;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.RfidTagId;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RfidTagAssignmentHistoryViews {

    RfidTagAssignmentHistoryViews MAPPER = Mappers.getMapper(RfidTagAssignmentHistoryViews.class);

    // ------------------------------------------------------------------------
    // mapper

    RfidTagAssignmentHistoryView toRfidTagAssignmentView(RfidTagAssignmentHistory rfidTagAssignment);

    // ------------------------------------------------------------------------
    // views

    record RfidTagAssignmentHistoryView( //
            @NotNull CustomerId customerId, //
            @NotNull RfidTagId rfidTagId, //
            @NotNull RfidTagAssignmentType assignmentType, //
            @NotNull Instant assignmentDate, //
            @NotNull Instant unassignmentDate //
    ) {}

}
