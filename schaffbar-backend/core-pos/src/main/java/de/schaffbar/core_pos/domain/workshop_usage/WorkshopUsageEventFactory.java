package de.schaffbar.core_pos.domain.workshop_usage;

import de.schaffbar.core_pos.shared.event.EventType;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class WorkshopUsageEventFactory {

    static SchaffbarEvent workshopUsageEntered(WorkshopUsage usage) {
        var payload = WorkshopUsagePayloadMapper.MAPPER.toWorkshopUsageEnteredPayload(usage);
        payload.validate();

        return SchaffbarEvent.workshopUsageEvent(EventType.WORKSHOP_USAGE_ENTERED, usage.getId(), payload);
    }

    static SchaffbarEvent workshopUsageLeft(WorkshopUsage usage) {
        var payload = WorkshopUsagePayloadMapper.MAPPER.toWorkshopUsageLeftPayload(usage);
        payload.validate();

        return SchaffbarEvent.workshopUsageEvent(EventType.WORKSHOP_USAGE_LEFT, usage.getId(), payload);
    }

}
