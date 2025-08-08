package de.schaffbar.core_pos.rfid_tag_assignment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.RfidTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RfidTagAssignmentRepository extends JpaRepository<RfidTagAssignment, UUID> {

    List<RfidTagAssignment> findByCustomerId(UUID customerId);

    default Optional<RfidTagAssignment> findByCustomer(CustomerId customerId) {
        List<RfidTagAssignment> assignments = findByCustomerId(customerId.getValue());
        if (assignments.isEmpty()) {
            return Optional.empty();
        }

        if (assignments.size() > 1) {
            throw new RuntimeException("More than one rfid tag assignment found");
        }

        return Optional.of(assignments.getFirst());
    }

    List<RfidTagAssignment> findByRfidTagId(String rfidTagId);

    default Optional<RfidTagAssignment> findByRfidTag(RfidTagId rfidTagId) {
        List<RfidTagAssignment> assignments = findByRfidTagId(rfidTagId.getValue());
        if (assignments.isEmpty()) {
            return Optional.empty();
        }

        if (assignments.size() > 1) {
            throw new RuntimeException("More than one rfid tag assignment found");
        }

        return Optional.of(assignments.getFirst());
    }

    List<RfidTagAssignment> findByStatus(RfidTagAssignmentStatus status);

    default Optional<RfidTagAssignment> findWaitingForAssignment() {
        List<RfidTagAssignment> assignments = findByStatus(RfidTagAssignmentStatus.WAITING_FOR_ASSIGNMENT);
        if (assignments.isEmpty()) {
            return Optional.empty();
        }

        if (assignments.size() > 1) {
            throw new RuntimeException("More than one rfid tag assignment found");
        }

        return Optional.of(assignments.getFirst());
    }

}
