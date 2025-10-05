package de.schaffbar.core_pos.workshop_session.web;

import static java.util.Objects.isNull;

import java.time.Duration;
import java.util.List;

import de.schaffbar.core_pos.ValueObjectMapper;
import de.schaffbar.core_pos.workshop_session.WorkshopSessionViews.WorkshopSessionView;
import de.schaffbar.core_pos.workshop_session.web.WorkshopSessionApiModel.WorkshopSessionApiDto;
import de.schaffbar.core_pos.workshop_session.web.WorkshopSessionApiModel.WorkshopUsageApiDto;
import de.schaffbar.core_pos.workshop_usage.WorkshopUsageViews.WorkshopUsageView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface WorkshopSessionApiMapper extends ValueObjectMapper {

    WorkshopSessionApiMapper MAPPER = Mappers.getMapper(WorkshopSessionApiMapper.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "id", source = "session.id.value")
    @Mapping(target = "customerId", source = "session.customerId.value")
    @Mapping(target = "workshopUsages", source = "workshopUsages")
    WorkshopSessionApiDto toWorkshopSessionApiDto(WorkshopSessionView session, List<WorkshopUsageView> workshopUsages);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "durationInMinutes", source = "duration", qualifiedByName = "toDurationInMinutes")
    WorkshopUsageApiDto toWorkshopUsageApiDto(WorkshopUsageView workshopUsage);

    // ------------------------------------------------------------------------
    // mapping request body to command

    // ------------------------------------------------------------------------
    // helper

    @Named("toDurationInMinutes")
    default Long toDurationInMinutes(Duration duration) {
        if (isNull(duration)) {
            return null;
        }

        return duration.toMinutes();
    }

}
