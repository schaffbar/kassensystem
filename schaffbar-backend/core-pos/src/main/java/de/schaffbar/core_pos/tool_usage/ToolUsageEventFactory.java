package de.schaffbar.core_pos.tool_usage;

import de.schaffbar.core_pos.shared.event.EventType;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ToolUsageEventFactory {

    static SchaffbarEvent toolUsageStarted(ToolUsage toolUsage) {
        var payload = ToolUsagePayloadMapper.MAPPER.toToolUsageStartedPayload(toolUsage);
        payload.validate();

        return SchaffbarEvent.toolUsageEvent(EventType.TOOL_USAGE_STARTED, toolUsage.getId(), payload);
    }

    static SchaffbarEvent toolUsageStopped(ToolUsage toolUsage) {
        var payload = ToolUsagePayloadMapper.MAPPER.toToolUsageStoppedPayload(toolUsage);
        payload.validate();

        return SchaffbarEvent.toolUsageEvent(EventType.TOOL_USAGE_STOPPED, toolUsage.getId(), payload);
    }

}
