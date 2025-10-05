package de.schaffbar.core_pos.workshop_session;

import java.util.Optional;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.workshop_session.WorkshopSessionViews.WorkshopSessionView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class WorkshopSessionService {

    private final @NonNull WorkshopSessionRepository workshopSessionRepository;

    // ------------------------------------------------------------------------
    // query

    public Optional<WorkshopSessionView> getOpenWorkshopSession(@NotNull @Valid CustomerId customerId) {
        return fetchOpenWorkshopSession(customerId) //
                .map(WorkshopSessionViewMapper.MAPPER::toWorkshopSessionView);
    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public void startSession(@NotNull @Valid CustomerId customerId) {
        fetchOpenWorkshopSession(customerId) //
                .ifPresent(this::throwHasAlreadyOpenWorkshopSession);

        WorkshopSession workshopSession = WorkshopSession.of(customerId);
        this.workshopSessionRepository.save(workshopSession);
    }

    @Transactional
    public void closeSession(@NotNull @Valid CustomerId customerId) {
        fetchOpenWorkshopSession(customerId) //
                .ifPresentOrElse( //
                        WorkshopSession::close, //
                        throwNoWorkshopSessionFound(customerId));
    }

    // ------------------------------------------------------------------------
    // helper

    private Optional<WorkshopSession> fetchOpenWorkshopSession(CustomerId customerId) {
        return this.workshopSessionRepository.findOpenWorkshopSession(customerId);
    }

    private void throwHasAlreadyOpenWorkshopSession(WorkshopSession session) {
        throw new RuntimeException("Customer [id: " + session.getCustomerId() + "] has already open workshop session");
    }

    private Runnable throwNoWorkshopSessionFound(CustomerId customerId) {
        return () -> {
            throw new RuntimeException("No workshop session found for customer [id: " + customerId + "]");
        };
    }

}
