package de.schaffbar.core_pos.dashboard.web;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.dashboard.WorkshopDashboardViews.WorkshopDashboardEntryView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface WorkshopDashboardApiModel {

    WorkshopDashboardApiModel MAPPER = Mappers.getMapper(WorkshopDashboardApiModel.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "customerId", source = "customerId.value")
    WorkshopDashboardEntryApiDto toWorkshopDashboardEntryApiDto(WorkshopDashboardEntryView view);

    // ------------------------------------------------------------------------
    // response

    record WorkshopDashboardEntryApiDto( //
            @NotNull UUID customerId, //
            @NotBlank String firstName, //
            @NotBlank String lastName, //
            @NotNull @PastOrPresent Instant entryTime //
    ) {}

}
