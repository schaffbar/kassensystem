package de.schaffbar.core_pos.rfid_tag_assignment;

import de.schaffbar.core_pos.shared.event.EventType;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class RfidTagAssignmentEventFactory {

    static SchaffbarEvent rfidTagAssignmentRequested(RfidTagAssignment assignment) {
        var payload = RfidTagAssignmentPayloadMapper.MAPPER.toRfidTagAssignmentRequestedPayload(assignment);
        payload.validate();

        return SchaffbarEvent.rfidTagAssignmentEvent(EventType.RFID_TAG_ASSIGNMENT_REQUESTED, assignment.getId(), payload);
    }

    static SchaffbarEvent rfidTagAssigned(RfidTagAssignment assignment) {
        var payload = RfidTagAssignmentPayloadMapper.MAPPER.toRfidTagAssignedPayload(assignment);
        payload.validate();

        return SchaffbarEvent.rfidTagAssignmentEvent(EventType.RFID_TAG_ASSIGNED, assignment.getId(), payload);
    }

    static SchaffbarEvent rfidTagUnassigned(RfidTagAssignment assignment) {
        var payload = RfidTagAssignmentPayloadMapper.MAPPER.toRfidTagUnassignedPayload(assignment);
        payload.validate();

        return SchaffbarEvent.rfidTagAssignmentEvent(EventType.RFID_TAG_UNASSIGNED, assignment.getId(), payload);
    }

}
