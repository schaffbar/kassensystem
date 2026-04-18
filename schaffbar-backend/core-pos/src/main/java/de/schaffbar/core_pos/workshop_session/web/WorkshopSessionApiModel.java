package de.schaffbar.core_pos.workshop_session.web;

import static java.util.Objects.isNull;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool.ToolViews.ToolView;
import de.schaffbar.core_pos.tool_usage.ToolUsageViews.ToolUsageView;
import de.schaffbar.core_pos.workshop_session.WorkshopSessionStatus;
import de.schaffbar.core_pos.workshop_session.WorkshopSessionViews.WorkshopSessionView;
import de.schaffbar.core_pos.workshop_usage.WorkshopUsageViews.WorkshopUsageView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
    @Mapping(target = "toolUsageSummaries", source = "toolUsageSummaries")
    WorkshopSessionApiDto toWorkshopSessionApiDto(WorkshopSessionView session, List<WorkshopUsageView> workshopUsages,
            List<ToolUsageSummaryApiDto> toolUsageSummaries);

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "durationInMinutes", source = "durationInMinutes")
    @Mapping(target = "unitsUsed", source = "unitsUsed")
    WorkshopUsageApiDto toWorkshopUsageApiDto(WorkshopUsageView workshopUsage);

    // ------------------------------------------------------------------------
    // tool usage grouping logic

    default List<ToolUsageSummaryApiDto> toToolUsageSummaries(List<ToolUsageView> toolUsages, Map<ToolId, ToolView> toolMap) {
        return toolUsages.stream() //
                .collect(Collectors.groupingBy(ToolUsageView::toolId)) //
                .entrySet().stream() //
                .map(entry -> {
                    ToolId toolId = entry.getKey();
                    List<ToolUsageView> usagesForTool = entry.getValue();
                    ToolView tool = toolMap.get(toolId);

                    String toolName = (tool != null) ? tool.name() : null;

                    List<ToolUsageDetailApiDto> details = usagesForTool.stream() //
                            .map(this::toToolUsageDetailApiDto) //
                            .toList();

                    long totalDurationMinutes = usagesForTool.stream() //
                            .mapToLong(u -> {
                                Long mins = u.getDurationInMinutes();
                                if (mins != null) {
                                    return mins;
                                }
                                // active usage: calculate from startTime until now
                                long seconds = Duration.between(u.startTime().truncatedTo(ChronoUnit.SECONDS),
                                        Instant.now().truncatedTo(ChronoUnit.SECONDS)).getSeconds();
                                return (long) Math.ceil(seconds / 60.0);
                            }) //
                            .sum();

                    long totalUnits = usagesForTool.stream() //
                            .mapToLong(u -> {
                                Long units = u.getUnitsUsed();
                                if (units != null) {
                                    return units;
                                }
                                // active usage: calculate from startTime until now
                                long seconds = Duration.between(u.startTime().truncatedTo(ChronoUnit.SECONDS),
                                        Instant.now().truncatedTo(ChronoUnit.SECONDS)).getSeconds();
                                long totalMinutes = (long) Math.ceil(seconds / 60.0);
                                return (long) Math.ceil(totalMinutes / 6.0);
                            }) //
                            .sum();

                    boolean active = usagesForTool.stream().anyMatch(u -> isNull(u.endTime()));

                    return new ToolUsageSummaryApiDto(toolId.getValue().toString(), toolName, active, totalDurationMinutes, totalUnits, details);
                }) //
                .toList();
    }

    default ToolUsageDetailApiDto toToolUsageDetailApiDto(ToolUsageView usage) {
        return new ToolUsageDetailApiDto( //
                usage.id().getValue().toString(), //
                usage.startTime(), //
                usage.endTime(), //
                usage.getDurationInMinutes(), //
                usage.getUnitsUsed() //
        );
    }

    // ------------------------------------------------------------------------
    // mapping request body to command

    // ------------------------------------------------------------------------
    // response

    record WorkshopSessionApiDto( //
            @NotBlank String id, //
            @NotBlank String customerId, //
            @NotNull Instant startTime, //
            Instant closeTime, //
            WorkshopSessionStatus status, //
            List<WorkshopUsageApiDto> workshopUsages, //
            List<ToolUsageSummaryApiDto> toolUsageSummaries //
    ) {}

    record WorkshopUsageApiDto( //
            @NotNull String id, //
            @NotNull @PastOrPresent Instant entryTime, //
            @PastOrPresent Instant exitTime, //
            @Positive Long durationInMinutes, //
            @Positive Long unitsUsed //
    ) {}

    record ToolUsageSummaryApiDto( //
            @NotBlank String toolId, //
            String toolName, //
            boolean active, //
            @Positive Long totalDurationInMinutes, //
            @Positive Long totalUnits, //
            List<ToolUsageDetailApiDto> usages //
    ) {}

    record ToolUsageDetailApiDto( //
            @NotBlank String id, //
            @NotNull @PastOrPresent Instant startTime, //
            @PastOrPresent Instant endTime, //
            @Positive Long durationInMinutes, //
            @Positive Long unitsUsed //
    ) {}

    // ------------------------------------------------------------------------
    // request body

}
