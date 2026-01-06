package de.schaffbar.core_pos.workshop_usage;

import static java.util.Objects.isNull;

import java.time.Duration;
import java.time.Instant;

import de.schaffbar.core_pos.id.CustomerId;
import de.schaffbar.core_pos.id.WorkshopSessionId;
import de.schaffbar.core_pos.id.WorkshopUsageId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface WorkshopUsageViews {

    WorkshopUsageViews MAPPER = Mappers.getMapper(WorkshopUsageViews.class);

    // ------------------------------------------------------------------------
    // mapper

    WorkshopUsageView toWorkshopUsageView(WorkshopUsage workshopUsage);

    // ------------------------------------------------------------------------
    // views

    record WorkshopUsageView( //
            @NotNull WorkshopUsageId id, //
            @NotNull CustomerId customerId, //
            @NotNull WorkshopSessionId workshopSessionId, //
            @NotNull @PastOrPresent Instant entryTime, //
            @PastOrPresent Instant exitTime, //
            Duration duration //
    ) {

        public Long getDurationInMinutes() {
            if (isNull(this.duration)) {
                return null;
            }

            // Each started second is one minute
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
