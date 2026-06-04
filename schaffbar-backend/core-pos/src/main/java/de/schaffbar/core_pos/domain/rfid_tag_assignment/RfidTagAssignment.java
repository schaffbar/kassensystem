package de.schaffbar.core_pos.domain.rfid_tag_assignment;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.RfidTagAssignmentId;
import de.schaffbar.core_pos.shared.id.RfidTagId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "RFID_TAG_ASSIGNMENT", schema = "SCHAFFBAR")
class RfidTagAssignment {

    @Id
    private UUID id;

    @NotNull
    @Column(unique = true)
    private UUID customerId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RfidTagAssignmentType assignmentType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RfidTagAssignmentStatus status;

    @Column(unique = true)
    private String rfidTagId;

    private Instant assignmentDate;

    @Version
    private Instant updatedAt;

    // ------------------------------------------------------------------------
    // static constructor

    public static RfidTagAssignment of(CustomerId customerId, RfidTagAssignmentType assignmentType) {
        RfidTagAssignment result = new RfidTagAssignment();
        result.setId(RfidTagAssignmentId.random().getValue());
        result.setCustomerId(customerId.getValue());
        result.setAssignmentType(assignmentType);
        result.setStatus(RfidTagAssignmentStatus.WAITING_FOR_ASSIGNMENT);

        return result;
    }

    // ------------------------------------------------------------------------
    // query

    public RfidTagAssignmentId getId() {
        return RfidTagAssignmentId.of(this.id);
    }

    public CustomerId getCustomerId() {
        return CustomerId.of(this.customerId);
    }

    public RfidTagId getRfidTagId() {
        if (isBlank(this.rfidTagId)) {
            return null;
        }

        return RfidTagId.of(this.rfidTagId);
    }

    public boolean isAssigned() {
        return RfidTagAssignmentStatus.ASSIGNED == this.status;
    }

    // ------------------------------------------------------------------------
    // command

    public List<SchaffbarEvent> assignRfidTag(RfidTagId rfidTagId) {
        // TODO: check if assignment is in the correct state
        this.rfidTagId = rfidTagId.getValue();
        this.status = RfidTagAssignmentStatus.ASSIGNED;
        this.assignmentDate = Instant.now();

        return List.of(RfidTagAssignmentEventFactory.rfidTagAssigned(this));
    }

    public List<SchaffbarEvent> unassignEvents() {
        return List.of(RfidTagAssignmentEventFactory.rfidTagUnassigned(this));
    }

}
