package de.schaffbar.core_pos.workshop_usage;

import de.schaffbar.core_pos.workshop_usage.WorkshopUsageViews.WorkshopUsageView;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface WorkshopUsageViewMapper {

    WorkshopUsageViewMapper MAPPER = Mappers.getMapper(WorkshopUsageViewMapper.class);

    WorkshopUsageView toWorkshopUsageView(WorkshopUsage workshopUsage);

}
