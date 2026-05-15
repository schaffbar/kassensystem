package de.schaffbar.core_pos.tool_certification;

import static java.util.Objects.nonNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.exception.InvalidCertificationStateTransitionException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolCertificationId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool_certification.ToolCertificationCommands.CreateToolCertificationCommand;
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
@Table(name = "TOOL_CERTIFICATION", schema = "SCHAFFBAR")
class ToolCertification {

    @Id
    private UUID id;

    @NotNull
    private UUID customerId;

    @NotNull
    private UUID toolId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ToolCertificationStatus status;

    @NotNull
    private Instant certifiedAt;

    private UUID certifiedBy;

    @NotNull
    @Version
    private Instant updatedAt;

    // ------------------------------------------------------------------------
    // static constructor

    public static ToolCertification of(CreateToolCertificationCommand command) {
        ToolCertification certification = new ToolCertification();
        certification.setId(ToolCertificationId.random().getValue());
        certification.setCustomerId(command.customerId().getValue());
        certification.setToolId(command.toolId().getValue());
        certification.setStatus(ToolCertificationStatus.ACTIVE);
        certification.setCertifiedAt(Instant.now());

        if (nonNull(command.certifiedBy())) {
            certification.setCertifiedBy(command.certifiedBy().getValue());
        }

        return certification;
    }

    // ------------------------------------------------------------------------
    // query

    public ToolCertificationId getId() {
        return ToolCertificationId.of(this.id);
    }

    public CustomerId getCustomerId() {
        return CustomerId.of(this.customerId);
    }

    public ToolId getToolId() {
        return ToolId.of(this.toolId);
    }

    public CustomerId getCertifiedBy() {
        return nonNull(this.certifiedBy) ? CustomerId.of(this.certifiedBy) : null;
    }

    // ------------------------------------------------------------------------
    // command

    public List<SchaffbarEvent> pause() {
        if (this.status != ToolCertificationStatus.ACTIVE) {
            throw new InvalidCertificationStateTransitionException(this.getId(), this.status.name(), ToolCertificationStatus.PAUSED.name());
        }

        this.setStatus(ToolCertificationStatus.PAUSED);

        return List.of(ToolCertificationEventFactory.toolCertificationPaused(this));
    }

    public List<SchaffbarEvent> reactivate() {
        if (this.status != ToolCertificationStatus.PAUSED) {
            throw new InvalidCertificationStateTransitionException(this.getId(), this.status.name(), ToolCertificationStatus.ACTIVE.name());
        }

        this.setStatus(ToolCertificationStatus.ACTIVE);

        return List.of(ToolCertificationEventFactory.toolCertificationReactivated(this));
    }

    public List<SchaffbarEvent> revoke() {
        if (this.status == ToolCertificationStatus.REVOKED) {
            throw new InvalidCertificationStateTransitionException(this.getId(), this.status.name(), ToolCertificationStatus.REVOKED.name());
        }

        this.setStatus(ToolCertificationStatus.REVOKED);

        return List.of(ToolCertificationEventFactory.toolCertificationRevoked(this));
    }

}
