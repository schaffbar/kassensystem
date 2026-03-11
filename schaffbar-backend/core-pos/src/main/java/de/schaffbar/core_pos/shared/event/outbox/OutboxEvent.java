package de.schaffbar.core_pos.shared.event.outbox;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.shared.event.AggregateType;
import de.schaffbar.core_pos.shared.event.EventType;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent.EventVersion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "OUTBOX_EVENT", schema = "SCHAFFBAR")
public class OutboxEvent {

    @Id
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EventVersion version;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AggregateType aggregateType;

    @NotBlank
    private String aggregateId;

    @NotNull
    private Instant createdAt;

    @NotNull
    @Column(columnDefinition = "TEXT") // TODO: maybe JSONB if supported by the database
    private String payload;

    private String owner;

    private boolean processed;

    private Instant processedAt;

    // ------------------------------------------------------------------------
    // static factory method

    public static OutboxEvent of(SchaffbarEvent event) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setId(event.getId());
        outboxEvent.setEventType(event.getType());
        outboxEvent.setVersion(event.getVersion());
        outboxEvent.setAggregateType(event.getAggregateType());
        outboxEvent.setAggregateId(event.getAggregateId());
        outboxEvent.setCreatedAt(event.getTimestamp());
        outboxEvent.setPayload(event.serialize()); // TODO: whole event as JSON, not just payload ???
        outboxEvent.setOwner(event.getOwner());
        outboxEvent.setProcessed(false);

        return outboxEvent;
    }

    // ------------------------------------------------------------------------
    // command

    public void markAsProcessed() {
        this.processed = true;
        this.processedAt = Instant.now();
    }

}
