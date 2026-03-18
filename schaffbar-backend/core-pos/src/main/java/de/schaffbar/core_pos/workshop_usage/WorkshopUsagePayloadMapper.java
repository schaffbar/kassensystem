package de.schaffbar.core_pos.workshop_usage;

import de.schaffbar.core_pos.shared.event.payload.WorkshopUsagePayloads.WorkshopUsageEnteredPayload;
import de.schaffbar.core_pos.shared.event.payload.WorkshopUsagePayloads.WorkshopUsageLeftPayload;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface WorkshopUsagePayloadMapper {

    WorkshopUsagePayloadMapper MAPPER = Mappers.getMapper(WorkshopUsagePayloadMapper.class);

    // ------------------------------------------------------------------------
    // mapper

    WorkshopUsageEnteredPayload toWorkshopUsageEnteredPayload(WorkshopUsage usage);

    WorkshopUsageLeftPayload toWorkshopUsageLeftPayload(WorkshopUsage usage);

}
