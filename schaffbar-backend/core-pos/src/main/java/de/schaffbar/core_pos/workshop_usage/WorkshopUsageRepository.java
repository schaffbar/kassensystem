package de.schaffbar.core_pos.workshop_usage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.WorkshopSessionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface WorkshopUsageRepository extends JpaRepository<WorkshopUsage, UUID> {

    List<WorkshopUsage> findByCustomerIdAndExitTimeIsNull(UUID customerId);

    default Optional<WorkshopUsage> findActiveByCustomerId(CustomerId customerId) {
        List<WorkshopUsage> workshopUsages = findByCustomerIdAndExitTimeIsNull(customerId.getValue());
        if (workshopUsages.isEmpty()) {
            return Optional.empty();
        }

        if (workshopUsages.size() > 1) {
            throw new RuntimeException("More than one open workshop usage found");
        }

        return Optional.of(workshopUsages.getFirst());
    }

    List<WorkshopUsage> findWorkshopUsageByWorkshopSessionId(UUID workshopSessionId);

    default List<WorkshopUsage> findWorkshopUsages(WorkshopSessionId workshopSessionId) {
        return findWorkshopUsageByWorkshopSessionId(workshopSessionId.getValue());
    }

}
