package de.schaffbar.core_pos.workshop_usage;

import static java.util.Objects.isNull;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import de.schaffbar.core_pos.id.CustomerId;
import de.schaffbar.core_pos.id.WorkshopSessionId;
import de.schaffbar.core_pos.id.WorkshopUsageId;
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
@Table(name = "WORKSHOP_USAGE", schema = "SCHAFFBAR")
public
class WorkshopUsage { // TODO: Consider renaming to WorkshopSlot, or UsageSlot for clarity.

    @Id
    private UUID id;

    @NotNull
    private UUID customerId;

    @NotNull
    private UUID workshopSessionId;

    @NotNull
    private Instant entryTime;

    private Instant exitTime;

    @NotNull
    @Version
    private Instant updatedAt;

    // ------------------------------------------------------------------------
    // static constructor

    public static WorkshopUsage of(CustomerId customerId, WorkshopSessionId workshopSessionId) {
        WorkshopUsage workshopUsage = new WorkshopUsage();
        workshopUsage.setId(UUID.randomUUID());
        workshopUsage.setCustomerId(customerId.getValue());
        workshopUsage.setWorkshopSessionId(workshopSessionId.getValue());
        workshopUsage.setEntryTime(Instant.now());

        return workshopUsage;
    }

    // ------------------------------------------------------------------------
    // query

    public WorkshopUsageId getId() {
        return WorkshopUsageId.of(this.id);
    }

    public CustomerId getCustomerId() {
        return CustomerId.of(this.customerId);
    }

    public WorkshopSessionId getWorkshopSessionId() {
        return WorkshopSessionId.of(this.workshopSessionId);
    }

    public Duration getDuration() {
        if (isNull(this.entryTime) || isNull(this.exitTime)) {
            return null;
        }

        return Duration.between(this.entryTime.truncatedTo(ChronoUnit.SECONDS), this.exitTime.truncatedTo(ChronoUnit.SECONDS));
    }

    // ------------------------------------------------------------------------
    // command

    public void exit() {
        this.setExitTime(Instant.now());
    }

}
