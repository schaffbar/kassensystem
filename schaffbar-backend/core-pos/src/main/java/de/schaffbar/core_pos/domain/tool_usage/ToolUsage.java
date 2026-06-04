package de.schaffbar.core_pos.domain.tool_usage;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import de.schaffbar.core_pos.domain.tool_usage.ToolUsageCommands.StartToolUsageCommand;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.exception.ToolAlreadyStoppedException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.shared.id.ToolUsageId;
import de.schaffbar.core_pos.shared.id.WorkshopSessionId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "TOOL_USAGE", schema = "SCHAFFBAR")
class ToolUsage {

    @Id
    private UUID id;

    @NotNull
    private UUID customerId;

    @NotNull
    private UUID toolId;

    @NotNull
    private UUID workshopSessionId;

    @NotNull
    private Instant startTime;

    private Instant endTime;

    @NotNull
    @Version
    private Instant updatedAt;

    // ------------------------------------------------------------------------
    // static constructor

    public static ToolUsage of(StartToolUsageCommand command) {
        ToolUsage toolUsage = new ToolUsage();
        toolUsage.setId(ToolUsageId.random().getValue());
        toolUsage.setCustomerId(command.customerId().getValue());
        toolUsage.setToolId(command.toolId().getValue());
        toolUsage.setWorkshopSessionId(command.workshopSessionId().getValue());
        toolUsage.setStartTime(Instant.now());

        return toolUsage;
    }

    // ------------------------------------------------------------------------
    // query

    public ToolUsageId getId() {
        return ToolUsageId.of(this.id);
    }

    public CustomerId getCustomerId() {
        return CustomerId.of(this.customerId);
    }

    public ToolId getToolId() {
        return ToolId.of(this.toolId);
    }

    public WorkshopSessionId getWorkshopSessionId() {
        return WorkshopSessionId.of(this.workshopSessionId);
    }

    public Duration getDuration() {
        if (isNull(this.startTime) || isNull(this.endTime)) {
            return null;
        }

        return Duration.between(this.startTime.truncatedTo(ChronoUnit.SECONDS), this.endTime.truncatedTo(ChronoUnit.SECONDS));
    }

    // ------------------------------------------------------------------------
    // command

    public List<SchaffbarEvent> stop() {
        if (nonNull(this.endTime)) {
            throw new ToolAlreadyStoppedException(this.getId());
        }

        this.setEndTime(Instant.now());

        return List.of(ToolUsageEventFactory.toolUsageStopped(this));
    }

    // ------------------------------------------------------------------------
    // helper

}
