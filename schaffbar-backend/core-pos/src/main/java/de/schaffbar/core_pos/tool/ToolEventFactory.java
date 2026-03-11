package de.schaffbar.core_pos.tool;

import de.schaffbar.core_pos.shared.event.EventType;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.event.tool.ToolPayloads.ToolDeletedPayload;
import de.schaffbar.core_pos.shared.event.tool.ToolPayloads.ToolRfidReaderAssignedPayload;
import de.schaffbar.core_pos.shared.event.tool.ToolPayloads.ToolRfidReaderClearedPayload;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ToolId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ToolEventFactory {

    static SchaffbarEvent toolCreated(Tool tool) {
        var payload = ToolPayloadMapper.MAPPER.toToolCreatedPayload(tool);
        payload.validate();

        return SchaffbarEvent.toolEvent(EventType.TOOL_CREATED, tool.getId(), payload);
    }

    static SchaffbarEvent toolUpdated(Tool tool) {
        var payload = ToolPayloadMapper.MAPPER.toToolUpdatedPayload(tool);
        payload.validate();

        return SchaffbarEvent.toolEvent(EventType.TOOL_UPDATED, tool.getId(), payload);
    }

    static SchaffbarEvent toolWlanRelaisUpdated(Tool tool) {
        var payload = ToolPayloadMapper.MAPPER.toToolWlanRelaisUpdatedPayload(tool);
        payload.validate();

        return SchaffbarEvent.toolEvent(EventType.TOOL_WLAN_RELAIS_UPDATED, tool.getId(), payload);
    }

    static SchaffbarEvent toolRfidReaderAssigned(ToolId toolId, RfidReaderId rfidReaderId) {
        var payload = new ToolRfidReaderAssignedPayload(toolId, rfidReaderId);
        payload.validate();

        return SchaffbarEvent.toolEvent(EventType.TOOL_RFID_READER_ASSIGNED, toolId, payload);
    }

    static SchaffbarEvent toolRfidReaderCleared(ToolId toolId) {
        var payload = new ToolRfidReaderClearedPayload(toolId);
        payload.validate();

        return SchaffbarEvent.toolEvent(EventType.TOOL_RFID_READER_CLEARED, toolId, payload);
    }

    static SchaffbarEvent toolDeleted(ToolId toolId) {
        var payload = new ToolDeletedPayload(toolId);
        payload.validate();

        return SchaffbarEvent.toolEvent(EventType.TOOL_DELETED, toolId, payload);
    }

}
