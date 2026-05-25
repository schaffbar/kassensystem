package de.schaffbar.core_pos.tool;

import java.util.List;

import de.schaffbar.core_pos.shared.event.EventType;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.event.payload.ToolEventPayload.ToolDeletedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolEventPayload.ToolInstructorsAddedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolEventPayload.ToolInstructorsRemovedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolEventPayload.ToolRfidReaderAssignedPayload;
import de.schaffbar.core_pos.shared.event.payload.ToolEventPayload.ToolRfidReaderClearedPayload;
import de.schaffbar.core_pos.shared.id.CustomerId;
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

    static SchaffbarEvent toolInstructorsAdded(ToolId toolId, List<CustomerId> instructorIds) {
        var payload = new ToolInstructorsAddedPayload(toolId, instructorIds);
        payload.validate();

        return SchaffbarEvent.toolEvent(EventType.TOOL_INSTRUCTORS_ADDED, toolId, payload);
    }

    static SchaffbarEvent toolInstructorsRemoved(ToolId toolId, List<CustomerId> instructorIds) {
        var payload = new ToolInstructorsRemovedPayload(toolId, instructorIds);
        payload.validate();

        return SchaffbarEvent.toolEvent(EventType.TOOL_INSTRUCTORS_REMOVED, toolId, payload);
    }

    static SchaffbarEvent toolDeleted(ToolId toolId) {
        var payload = new ToolDeletedPayload(toolId);
        payload.validate();

        return SchaffbarEvent.toolEvent(EventType.TOOL_DELETED, toolId, payload);
    }

}
