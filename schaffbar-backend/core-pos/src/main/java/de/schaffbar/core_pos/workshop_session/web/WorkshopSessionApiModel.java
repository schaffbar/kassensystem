package de.schaffbar.core_pos.workshop_session.web;

import static java.util.Objects.isNull;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import de.schaffbar.core_pos.workshop_session.WorkshopSessionStatus;
import de.schaffbar.core_pos.workshop_session.WorkshopSessionViews.WorkshopSessionView;
import de.schaffbar.core_pos.workshop_usage.WorkshopUsageViews.WorkshopUsageView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface WorkshopSessionApiModel {

    WorkshopSessionApiModel MAPPER = Mappers.getMapper(WorkshopSessionApiModel.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "id", source = "session.id.value")
    @Mapping(target = "customerId", source = "session.customerId.value")
    @Mapping(target = "workshopUsages", source = "workshopUsages")
    WorkshopSessionApiDto toWorkshopSessionApiDto(WorkshopSessionView session, List<WorkshopUsageView> workshopUsages);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "durationInMinutes", source = "duration", qualifiedByName = "toDurationInMinutes")
    @Mapping(target = "unitsUsed", source = "duration", qualifiedByName = "toUnitsUsed")
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

    @Named("toUnitsUsed")
    default Long toUnitsUsed(Duration duration) {
        if (isNull(duration)) {
            return null;
        }

        // Each started 6 minutes is one unit
        long totalMinutes = duration.toMinutes();
        return (long) Math.ceil(totalMinutes / 6.0);
    }

    // ------------------------------------------------------------------------
    // response

    record WorkshopSessionApiDto( //
            @NotBlank String id, //
            @NotBlank String customerId, //
            @NotNull Instant startTime, //
            Instant closeTime, //
            WorkshopSessionStatus status, //
            List<WorkshopUsageApiDto> workshopUsages //
    ) {}

    record WorkshopUsageApiDto( //
            @NotNull String id, //
            @NotNull @PastOrPresent Instant entryTime, //
            @PastOrPresent Instant exitTime, //
            @Positive Long durationInMinutes, //
            @Positive Long unitsUsed //
    ) {}

    // ------------------------------------------------------------------------
    // request body

}
