package de.schaffbar.core_pos.rfid_tag;

import de.schaffbar.core_pos.shared.event.EventType;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.event.rfid_tag.RfidTagPayloads;
import de.schaffbar.core_pos.shared.id.RfidTagId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class RfidTagEventFactory {

    static SchaffbarEvent rfidTagCreated(RfidTag rfidTag) {
        var payload = RfidTagPayloadMapper.MAPPER.toRfidTagCreatedPayload(rfidTag);
        payload.validate();

        return SchaffbarEvent.rfidTagEvent(EventType.RFID_TAG_CREATED, rfidTag.getId(), payload);
    }

    static SchaffbarEvent rfidTagDeleted(RfidTagId rfidTagId) {
        var payload = new RfidTagPayloads.RfidTagDeletedPayload(rfidTagId);
        payload.validate();

        return SchaffbarEvent.rfidTagEvent(EventType.RFID_TAG_DELETED, rfidTagId, payload);
    }

}
