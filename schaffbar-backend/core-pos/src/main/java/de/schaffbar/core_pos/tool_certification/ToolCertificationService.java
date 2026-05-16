package de.schaffbar.core_pos.tool_certification;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEventRepository;
import de.schaffbar.core_pos.shared.exception.CertificationAlreadyExistsException;
import de.schaffbar.core_pos.shared.exception.CertificationPermanentlyRevokedException;
import de.schaffbar.core_pos.shared.exception.CustomerNotCertifiedForToolException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolCertificationId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool_certification.ToolCertificationCommands.CreateToolCertificationCommand;
import de.schaffbar.core_pos.tool_certification.ToolCertificationViews.ToolCertificationView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ToolCertificationService {

    private final @NonNull ToolCertificationRepository toolCertificationRepository;

    private final @NonNull OutboxEventRepository outboxEventRepository;

    // ------------------------------------------------------------------------
    // query

    public List<ToolCertificationView> getCertifications(@NotNull @Valid CustomerId customerId) {
        return this.toolCertificationRepository.findByCustomerId(customerId.getValue()).stream() //
                .map(ToolCertificationViews.MAPPER::toToolCertificationView) //
                .toList();
    }

    public List<ToolCertificationView> getCertifications(@NotNull @Valid ToolId toolId) {
        return this.toolCertificationRepository.findByToolId(toolId.getValue()).stream() //
                .map(ToolCertificationViews.MAPPER::toToolCertificationView) //
                .toList();
    }

    public Optional<ToolCertificationView> getCertification(@NotNull @Valid ToolCertificationId id) {
        return this.toolCertificationRepository.findById(id.getValue()) //
                .map(ToolCertificationViews.MAPPER::toToolCertificationView);
    }

    public boolean hasActiveCertification(@NotNull @Valid CustomerId customerId, @NotNull @Valid ToolId toolId) {
        return this.toolCertificationRepository.findByCustomerIdAndToolIdAndStatus(customerId.getValue(), toolId.getValue(),
                ToolCertificationStatus.ACTIVE).isPresent();
    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public void create(@NotNull @Valid CreateToolCertificationCommand command) {
        ensureNoCertificationExists(command.customerId(), command.toolId());

        ToolCertification certification = ToolCertification.of(command);
        this.toolCertificationRepository.save(certification);

        List<SchaffbarEvent> events = List.of(ToolCertificationEventFactory.toolCertificationCreated(certification));
        saveOutboxEvents(events);

        log.info("Created tool certification for customer {} on tool {} and published events {}", command.customerId(), command.toolId(), events);
    }

    @Transactional
    public void pause(@NotNull @Valid CustomerId customerId, @NotNull @Valid ToolId toolId) {
        ToolCertification certification = findByCustomerAndToolOrThrow(customerId, toolId);

        List<SchaffbarEvent> events = certification.pause();
        saveOutboxEvents(events);

        log.info("Paused tool certification for customer {} on tool {} and published events {}", customerId, toolId, events);
    }

    @Transactional
    public void reactivate(@NotNull @Valid CustomerId customerId, @NotNull @Valid ToolId toolId) {
        ToolCertification certification = findByCustomerAndToolOrThrow(customerId, toolId);

        List<SchaffbarEvent> events = certification.reactivate();
        saveOutboxEvents(events);

        log.info("Reactivated tool certification for customer {} on tool {} and published events {}", customerId, toolId, events);
    }

    @Transactional
    public void revoke(@NotNull @Valid CustomerId customerId, @NotNull @Valid ToolId toolId) {
        ToolCertification certification = findByCustomerAndToolOrThrow(customerId, toolId);

        List<SchaffbarEvent> events = certification.revoke();
        saveOutboxEvents(events);

        log.info("Revoked tool certification for customer {} on tool {} and published events {}", customerId, toolId, events);
    }

    @Transactional
    public void delete(@NotNull @Valid CustomerId customerId, @NotNull @Valid ToolId toolId) {
        ToolCertification certification = findByCustomerAndToolOrThrow(customerId, toolId);

        List<SchaffbarEvent> events = List.of(ToolCertificationEventFactory.toolCertificationDeleted(certification));
        saveOutboxEvents(events);

        this.toolCertificationRepository.delete(certification);

        log.info("Deleted tool certification for customer {} on tool {} and published events {}", customerId, toolId, events);
    }

    // ------------------------------------------------------------------------
    // helper

    private ToolCertification findByCustomerAndToolOrThrow(CustomerId customerId, ToolId toolId) {
        return this.toolCertificationRepository.findByCustomerIdAndToolId(customerId.getValue(), toolId.getValue()) //
                .orElseThrow(() -> new CustomerNotCertifiedForToolException(customerId, toolId));
    }

    private void ensureNoCertificationExists(CustomerId customerId, ToolId toolId) {
        this.toolCertificationRepository.findByCustomerIdAndToolId(customerId.getValue(), toolId.getValue()) //
                .ifPresent(existing -> {
                    if (existing.getStatus() == ToolCertificationStatus.REVOKED) {
                        throw new CertificationPermanentlyRevokedException(customerId, toolId);
                    }
                    throw new CertificationAlreadyExistsException(customerId, toolId);
                });
    }

    private void saveOutboxEvents(List<SchaffbarEvent> events) {
        List<OutboxEvent> outboxEvents = events.stream() //
                .map(OutboxEvent::of) //
                .toList();

        this.outboxEventRepository.saveAll(outboxEvents);
    }

}
