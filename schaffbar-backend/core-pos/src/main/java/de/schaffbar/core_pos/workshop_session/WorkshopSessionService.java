package de.schaffbar.core_pos.workshop_session;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEventRepository;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.workshop_session.WorkshopSession.WorkshopSessionWithEvents;
import de.schaffbar.core_pos.workshop_session.WorkshopSessionViews.WorkshopSessionView;
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
public class WorkshopSessionService {

    private final @NonNull WorkshopSessionRepository workshopSessionRepository;

    private final @NonNull OutboxEventRepository outboxEventRepository;

    // ------------------------------------------------------------------------
    // query

    // TODO: naming - use either active or open session consistently
    public Optional<WorkshopSessionView> getOpenWorkshopSession(@NotNull @Valid CustomerId customerId) {
        return fetchOpenWorkshopSession(customerId) //
                .map(WorkshopSessionViews.MAPPER::toWorkshopSessionView);
    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public void startSession(@NotNull @Valid CustomerId customerId) {
        fetchOpenWorkshopSession(customerId) //
                .ifPresent(this::throwHasAlreadyOpenWorkshopSession);

        WorkshopSessionWithEvents result = WorkshopSession.of(customerId);
        this.workshopSessionRepository.save(result.session());

        saveOutboxEvents(result.events());

        log.info("Started workshop session for customer {} and published events {}", customerId, result.events());
    }

    @Transactional
    public void closeSession(@NotNull @Valid CustomerId customerId) {
        WorkshopSession session = fetchOpenWorkshopSession(customerId) //
                .orElseThrow(() -> new RuntimeException("No workshop session found for customer [id: " + customerId + "]"));

        List<SchaffbarEvent> events = session.close();
        saveOutboxEvents(events);

        log.info("Closed workshop session for customer {} and published events {}", customerId, events);
    }

    // ------------------------------------------------------------------------
    // helper

    private Optional<WorkshopSession> fetchOpenWorkshopSession(CustomerId customerId) {
        return this.workshopSessionRepository.findOpenWorkshopSession(customerId);
    }

    private void throwHasAlreadyOpenWorkshopSession(WorkshopSession session) {
        throw new RuntimeException("Customer [id: " + session.getCustomerId() + "] has already open workshop session");
    }

    // TODO: DRY: this method is duplicated in multiple services, maybe move to a common base class or utility class?
    private void saveOutboxEvents(List<SchaffbarEvent> events) {
        List<OutboxEvent> outboxEvents = events.stream() //
                .map(OutboxEvent::of) //
                .toList();

        this.outboxEventRepository.saveAll(outboxEvents);
    }

}
