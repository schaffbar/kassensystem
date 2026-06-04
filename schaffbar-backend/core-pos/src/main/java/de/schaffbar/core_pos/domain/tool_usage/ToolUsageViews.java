package de.schaffbar.core_pos.domain.tool_usage;

import static java.util.Objects.isNull;

import java.time.Duration;
import java.time.Instant;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.shared.id.ToolUsageId;
import de.schaffbar.core_pos.shared.id.WorkshopSessionId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ToolUsageViews {

    ToolUsageViews MAPPER = Mappers.getMapper(ToolUsageViews.class);

    // ------------------------------------------------------------------------
    // mapper

    ToolUsageView toToolUsageView(ToolUsage toolUsage);

    // ------------------------------------------------------------------------
    // views

    record ToolUsageView( //
            @NotNull ToolUsageId id, //
            @NotNull CustomerId customerId, //
            @NotNull ToolId toolId, //
            @NotNull WorkshopSessionId workshopSessionId, //
            @NotNull @PastOrPresent Instant startTime, //
            @PastOrPresent Instant endTime, //
            Duration duration //
    ) {

        public Long getDurationInMinutes() {
            if (isNull(this.duration)) {
                return null;
            }

            long totalSeconds = this.duration.getSeconds();
            return (long) Math.ceil(totalSeconds / 60.0);
        }

        public Long getUnitsUsed() {
            if (isNull(this.duration)) {
                return null;
            }

            // Each started 6 minutes is one unit
            long totalSeconds = this.duration.getSeconds();
            long totalMinutes = (long) Math.ceil(totalSeconds / 60.0);
            return (long) Math.ceil(totalMinutes / 6.0);
        }

    }

}
