package de.schaffbar.core_pos.tool;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.schaffbar.core_pos.RfidReaderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ToolRepository extends JpaRepository<Tool, UUID> {

    List<Tool> findByRfidReaderId(UUID rfidReaderId);

    default Optional<Tool> findByRfidReaderId(RfidReaderId rfidReaderId) {
        List<Tool> tools = findByRfidReaderId(rfidReaderId.getValue());
        if (tools.isEmpty()) {
            return Optional.empty();
        }

        if (tools.size() > 1) {
            throw new RuntimeException("More than one tools found");
        }

        return Optional.of(tools.getFirst());
    }

}
