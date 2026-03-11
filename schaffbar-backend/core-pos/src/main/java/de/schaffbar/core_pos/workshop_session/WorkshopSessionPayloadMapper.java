package de.schaffbar.core_pos.workshop_session;

import de.schaffbar.core_pos.shared.event.workshop_session.WorkshopSessionPayloads.WorkshopSessionClosedPayload;
import de.schaffbar.core_pos.shared.event.workshop_session.WorkshopSessionPayloads.WorkshopSessionStartedPayload;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface WorkshopSessionPayloadMapper {

    WorkshopSessionPayloadMapper MAPPER = Mappers.getMapper(WorkshopSessionPayloadMapper.class);

    // ------------------------------------------------------------------------
    // mapper

    WorkshopSessionStartedPayload toWorkshopSessionStartedPayload(WorkshopSession session);

    WorkshopSessionClosedPayload toWorkshopSessionClosedPayload(WorkshopSession session);

}
