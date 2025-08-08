package de.schaffbar.core_pos.rfid_tag_assignment;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.RfidTagAssignmentId;
import de.schaffbar.core_pos.RfidTagId;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentCommands.RequestRfidTagAssignmentCommand;
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

    @Column(unique = true)
    private String rfidTagId;

    private Instant assignmentDate;

    private Instant unassignmentDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RfidTagAssignmentStatus status;

    @Version
    private Instant updateTime;

    // ------------------------------------------------------------------------
    // static constructor

    public static RfidTagAssignment of(RequestRfidTagAssignmentCommand command) {
        RfidTagAssignment result = new RfidTagAssignment();
        result.setId(UUID.randomUUID());
        result.setCustomerId(command.customerId().getValue());
        result.setAssignmentType(command.assignmentType());
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

    // ------------------------------------------------------------------------
    // command

    public void assignRfidTag(RfidTagId rfidTagId) {
        // TODO: check if assignment is in the correct state
        this.rfidTagId = rfidTagId.getValue();
        this.status = RfidTagAssignmentStatus.ASSIGNED;
        this.assignmentDate = Instant.now();
    }

    public void requestUnassignment() {
        // TODO: check if assignment is in the correct state
        this.status = RfidTagAssignmentStatus.WAITING_FOR_UNASSIGNMENT;
    }

    public void unassignRfidTag() {
        // TODO: check if assignment is in the correct state
        this.unassignmentDate = Instant.now();
        this.status = RfidTagAssignmentStatus.UNASSIGNED;
    }

}
