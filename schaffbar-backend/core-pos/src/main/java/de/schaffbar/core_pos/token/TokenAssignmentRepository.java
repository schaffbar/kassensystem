package de.schaffbar.core_pos.token;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.TokenId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TokenAssignmentRepository extends JpaRepository<TokenAssignment, UUID> {

    List<TokenAssignment> findByCustomerId(UUID customerId);

    default Optional<TokenAssignment> findByCustomer(CustomerId customerId) {
        List<TokenAssignment> assignments = findByCustomerId(customerId.getValue());
        if (assignments.isEmpty()) {
            return Optional.empty();
        }

        if (assignments.size() > 1) {
            throw new RuntimeException("More than one token assignment found");
        }

        return Optional.of(assignments.getFirst());
    }

    List<TokenAssignment> findByTokenId(String tokenId);

    default Optional<TokenAssignment> findByToken(TokenId tokenId) {
        List<TokenAssignment> assignments = findByTokenId(tokenId.getValue());
        if (assignments.isEmpty()) {
            return Optional.empty();
        }

        if (assignments.size() > 1) {
            throw new RuntimeException("More than one token assignment found");
        }

        return Optional.of(assignments.getFirst());
    }

    List<TokenAssignment> findByStatus(TokenAssignmentStatus status);

    default Optional<TokenAssignment> findWaitingForAssignment() {
        List<TokenAssignment> assignments = findByStatus(TokenAssignmentStatus.WAITING_FOR_ASSIGNMENT);
        if (assignments.isEmpty()) {
            return Optional.empty();
        }

        if (assignments.size() > 1) {
            throw new RuntimeException("More than one token assignment found");
        }

        return Optional.of(assignments.getFirst());
    }

}
