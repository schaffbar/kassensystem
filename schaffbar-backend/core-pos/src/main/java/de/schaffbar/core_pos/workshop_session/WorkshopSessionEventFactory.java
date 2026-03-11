package de.schaffbar.core_pos.workshop_session;

import de.schaffbar.core_pos.shared.event.EventType;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class WorkshopSessionEventFactory {

    static SchaffbarEvent workshopSessionStarted(WorkshopSession session) {
        var payload = WorkshopSessionPayloadMapper.MAPPER.toWorkshopSessionStartedPayload(session);
        payload.validate();

        return SchaffbarEvent.workshopSessionEvent(EventType.WORKSHOP_SESSION_STARTED, session.getId(), payload);
    }

    static SchaffbarEvent workshopSessionClosed(WorkshopSession session) {
        var payload = WorkshopSessionPayloadMapper.MAPPER.toWorkshopSessionClosedPayload(session);
        payload.validate();

        return SchaffbarEvent.workshopSessionEvent(EventType.WORKSHOP_SESSION_CLOSED, session.getId(), payload);
    }

}
