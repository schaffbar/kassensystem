package de.schaffbar.core_pos.tool_dashboard.web;

import java.util.UUID;

import de.schaffbar.core_pos.tool_dashboard.ToolDashboardViews.ToolDashboardEntryView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ToolDashboardApiModel {

    ToolDashboardApiModel MAPPER = Mappers.getMapper(ToolDashboardApiModel.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "toolId", source = "toolId.value")
    @Mapping(target = "customerId", source = "customerId.value")
    ToolDashboardEntryApiDto toToolDashboardEntryApiDto(ToolDashboardEntryView view);

    // ------------------------------------------------------------------------
    // response

    record ToolDashboardEntryApiDto( //
            @NotNull UUID toolId, //
            @NotBlank String toolName, //
            @NotNull UUID customerId, //
            @NotBlank String firstName, //
            @NotBlank String lastName //
    ) {}

}
