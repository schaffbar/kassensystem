package de.schaffbar.core_pos.rfid_reader;

import de.schaffbar.core_pos.shared.event.EventType;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.event.payload.RfidReaderEventPayload.RfidReaderDeletedPayload;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class RfidReaderEventFactory {

    static SchaffbarEvent rfidReaderCreated(RfidReader rfidReader) {
        var payload = RfidReaderPayloadMapper.MAPPER.toRfidReaderCreatedPayload(rfidReader);
        payload.validate();

        return SchaffbarEvent.rfidReaderEvent(EventType.RFID_READER_CREATED, rfidReader.getId(), payload);
    }

    static SchaffbarEvent rfidReaderUpdated(RfidReader rfidReader) {
        var payload = RfidReaderPayloadMapper.MAPPER.toRfidReaderUpdatedPayload(rfidReader);
        payload.validate();

        return SchaffbarEvent.rfidReaderEvent(EventType.RFID_READER_UPDATED, rfidReader.getId(), payload);
    }

    static SchaffbarEvent rfidReaderTypeChanged(RfidReader rfidReader) {
        var payload = RfidReaderPayloadMapper.MAPPER.toRfidReaderTypeChangedPayload(rfidReader);
        payload.validate();

        return SchaffbarEvent.rfidReaderEvent(EventType.RFID_READER_TYPE_CHANGED, rfidReader.getId(), payload);
    }

    static SchaffbarEvent rfidReaderDeleted(RfidReaderId rfidReaderId) {
        var payload = new RfidReaderDeletedPayload(rfidReaderId);
        payload.validate();

        return SchaffbarEvent.rfidReaderEvent(EventType.RFID_READER_DELETED, rfidReaderId, payload);
    }

}
