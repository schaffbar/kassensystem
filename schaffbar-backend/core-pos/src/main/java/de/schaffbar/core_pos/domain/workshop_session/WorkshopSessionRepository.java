package de.schaffbar.core_pos.domain.workshop_session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.schaffbar.core_pos.shared.id.CustomerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface WorkshopSessionRepository extends JpaRepository<WorkshopSession, UUID> {

    List<WorkshopSession> findByCustomerIdAndStatus(UUID customerId, WorkshopSessionStatus status);

    default Optional<WorkshopSession> findOpenWorkshopSession(CustomerId customerId) {
        List<WorkshopSession> workshopSessions = findByCustomerIdAndStatus(customerId.getValue(), WorkshopSessionStatus.OPEN);
        if (workshopSessions.isEmpty()) {
            return Optional.empty();
        }

        if (workshopSessions.size() > 1) {
            throw new RuntimeException("More than one open session found"); // TODO: create custom exception
        }

        return Optional.of(workshopSessions.getFirst());
    }

}
