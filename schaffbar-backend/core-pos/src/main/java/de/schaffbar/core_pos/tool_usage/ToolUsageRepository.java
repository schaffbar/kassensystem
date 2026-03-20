package de.schaffbar.core_pos.tool_usage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.shared.id.WorkshopSessionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ToolUsageRepository extends JpaRepository<ToolUsage, UUID> {

    List<ToolUsage> findByCustomerIdAndEndTimeIsNull(UUID customerId);

    List<ToolUsage> findByCustomerIdAndToolIdAndEndTimeIsNull(UUID customerId, UUID toolId);

    List<ToolUsage> findByToolIdAndEndTimeIsNull(UUID toolId);

    List<ToolUsage> findByWorkshopSessionId(UUID workshopSessionId);

    long countByCustomerIdAndEndTimeIsNull(UUID customerId);

    // ------------------------------------------------------------------------
    // default methods

    default List<ToolUsage> findActiveByCustomerId(CustomerId customerId) {
        return findByCustomerIdAndEndTimeIsNull(customerId.getValue());
    }

    default Optional<ToolUsage> findActiveByCustomerIdAndToolId(CustomerId customerId, ToolId toolId) {
        List<ToolUsage> usages = findByCustomerIdAndToolIdAndEndTimeIsNull(customerId.getValue(), toolId.getValue());
        if (usages.isEmpty()) {
            return Optional.empty();
        }

        if (usages.size() > 1) {
            throw new RuntimeException("More than one active tool usage found for customer [id: " + customerId + "] and tool [id: " + toolId + "]");
        }

        return Optional.of(usages.getFirst());
    }

    default long countActiveByCustomerId(CustomerId customerId) {
        return countByCustomerIdAndEndTimeIsNull(customerId.getValue());
    }

    default List<ToolUsage> findByWorkshopSessionId(WorkshopSessionId workshopSessionId) {
        return findByWorkshopSessionId(workshopSessionId.getValue());
    }

    default Optional<ToolUsage> findActiveByToolId(ToolId toolId) {
        List<ToolUsage> usages = findByToolIdAndEndTimeIsNull(toolId.getValue());
        if (usages.isEmpty()) {
            return Optional.empty();
        }

        if (usages.size() > 1) {
            throw new RuntimeException("More than one active tool usage found for tool [id: " + toolId + "]");
        }

        return Optional.of(usages.getFirst());
    }

}
