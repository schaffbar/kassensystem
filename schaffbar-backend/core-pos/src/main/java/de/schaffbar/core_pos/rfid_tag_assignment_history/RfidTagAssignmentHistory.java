package de.schaffbar.core_pos.rfid_tag_assignment_history;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentType;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentViews.RfidTagAssignmentView;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.RfidTagAssignmentId;
import de.schaffbar.core_pos.shared.id.RfidTagId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
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
@Table(name = "RFID_TAG_ASSIGNMENT_HISTORY", schema = "SCHAFFBAR")
class RfidTagAssignmentHistory {

    @Id
    private UUID id;

    @NotNull
    private UUID customerId;

    @NotNull
    private String rfidTagId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RfidTagAssignmentType assignmentType;

    @NotNull
    @Past
    private Instant assignmentDate;

    @NotNull
    @PastOrPresent
    private Instant unassignmentDate;

    @Version
    private Instant updatedAt;

    // ------------------------------------------------------------------------
    // static constructor

    public static RfidTagAssignmentHistory of(RfidTagAssignmentView activeRfidTagAssignment) {
        RfidTagAssignmentHistory result = new RfidTagAssignmentHistory();
        result.setId(RfidTagAssignmentId.random().getValue()); // FIXME: change to RfidTagAssignmentHistoryId ???
        result.setCustomerId(activeRfidTagAssignment.customerId().getValue());
        result.setRfidTagId(activeRfidTagAssignment.rfidTagId().getValue());
        result.setAssignmentType(activeRfidTagAssignment.assignmentType());
        result.setAssignmentDate(activeRfidTagAssignment.assignmentDate());
        result.setUnassignmentDate(Instant.now());

        return result;
    }

    // ------------------------------------------------------------------------
    // query

    // FIXME: change to RfidTagAssignmentHistoryId ???
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

}
