package de.schaffbar.core_pos.tool_certification;

import de.schaffbar.core_pos.shared.event.EventType;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class ToolCertificationEventFactory {

    static SchaffbarEvent toolCertificationCreated(ToolCertification toolCertification) {
        var payload = ToolCertificationPayloadMapper.MAPPER.toToolCertificationCreatedPayload(toolCertification);
        payload.validate();

        return SchaffbarEvent.toolCertificationEvent(EventType.TOOL_CERTIFICATION_CREATED, toolCertification.getId(), payload);
    }

    static SchaffbarEvent toolCertificationPaused(ToolCertification toolCertification) {
        var payload = ToolCertificationPayloadMapper.MAPPER.toToolCertificationPausedPayload(toolCertification);
        payload.validate();

        return SchaffbarEvent.toolCertificationEvent(EventType.TOOL_CERTIFICATION_PAUSED, toolCertification.getId(), payload);
    }

    static SchaffbarEvent toolCertificationReactivated(ToolCertification toolCertification) {
        var payload = ToolCertificationPayloadMapper.MAPPER.toToolCertificationReactivatedPayload(toolCertification);
        payload.validate();

        return SchaffbarEvent.toolCertificationEvent(EventType.TOOL_CERTIFICATION_REACTIVATED, toolCertification.getId(), payload);
    }

    static SchaffbarEvent toolCertificationRevoked(ToolCertification toolCertification) {
        var payload = ToolCertificationPayloadMapper.MAPPER.toToolCertificationRevokedPayload(toolCertification);
        payload.validate();

        return SchaffbarEvent.toolCertificationEvent(EventType.TOOL_CERTIFICATION_REVOKED, toolCertification.getId(), payload);
    }

}
