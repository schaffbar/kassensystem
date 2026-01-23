package de.schaffbar.core_pos.rfid_tag_assignment;

import java.time.Instant;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.RfidTagAssignmentId;
import de.schaffbar.core_pos.shared.id.RfidTagId;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RfidTagAssignmentViews {

    RfidTagAssignmentViews MAPPER = Mappers.getMapper(RfidTagAssignmentViews.class);

    // ------------------------------------------------------------------------
    // mapper

    RfidTagAssignmentView toRfidTagAssignmentView(RfidTagAssignment rfidTagAssignment);

    // ------------------------------------------------------------------------
    // views

    record RfidTagAssignmentView( //
            @NotNull RfidTagAssignmentId id, //
            @NotNull CustomerId customerId, //
            @NotNull RfidTagAssignmentType assignmentType, //
            @NotNull RfidTagAssignmentStatus status, //
            RfidTagId rfidTagId, //
            Instant assignmentDate //
    ) {}

}
