package de.schaffbar.core_pos.domain.workshop_session;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.WorkshopSessionId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "WORKSHOP_SESSION", schema = "SCHAFFBAR")
class WorkshopSession {

    @Id
    private UUID id;

    @NotNull
    private UUID customerId;

    @NotNull
    private Instant startTime;

    private Instant closeTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    private WorkshopSessionStatus status;

    @NotNull
    @Version
    private Instant updatedAt;

    // ------------------------------------------------------------------------
    // static constructor

    public static WorkshopSession of(CustomerId customerId) {
        WorkshopSession workshopSession = new WorkshopSession();
        workshopSession.setId(WorkshopSessionId.random().getValue());
        workshopSession.setCustomerId(customerId.getValue());
        workshopSession.setStartTime(Instant.now());
        workshopSession.setStatus(WorkshopSessionStatus.OPEN);

        return workshopSession;
    }

    // ------------------------------------------------------------------------
    // query

    public WorkshopSessionId getId() {
        return WorkshopSessionId.of(this.id);
    }

    public CustomerId getCustomerId() {
        return CustomerId.of(this.customerId);
    }

    // ------------------------------------------------------------------------
    // command

    public List<SchaffbarEvent> close() {
        this.closeTime = Instant.now();
        this.status = WorkshopSessionStatus.PAID;

        return List.of(WorkshopSessionEventFactory.workshopSessionClosed(this));
    }

}
