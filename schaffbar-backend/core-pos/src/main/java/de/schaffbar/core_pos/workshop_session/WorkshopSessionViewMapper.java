package de.schaffbar.core_pos.workshop_session;

import de.schaffbar.core_pos.workshop_session.WorkshopSessionViews.WorkshopSessionView;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface WorkshopSessionViewMapper {

    WorkshopSessionViewMapper MAPPER = Mappers.getMapper(WorkshopSessionViewMapper.class);

    WorkshopSessionView toWorkshopSessionView(WorkshopSession workshopSession);

}
