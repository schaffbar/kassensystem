package de.schaffbar.core_pos.domain.tool_certification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ToolCertificationRepository extends JpaRepository<ToolCertification, UUID> {

    Optional<ToolCertification> findByCustomerIdAndToolId(UUID customerId, UUID toolId);

    Optional<ToolCertification> findByCustomerIdAndToolIdAndStatus(UUID customerId, UUID toolId, ToolCertificationStatus status);

    List<ToolCertification> findByCustomerId(UUID customerId);

    List<ToolCertification> findByToolId(UUID toolId);

    void deleteAllByToolId(UUID toolId);

}
