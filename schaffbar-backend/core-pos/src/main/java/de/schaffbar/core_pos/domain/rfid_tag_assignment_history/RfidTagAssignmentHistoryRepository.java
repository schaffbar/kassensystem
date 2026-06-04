package de.schaffbar.core_pos.domain.rfid_tag_assignment_history;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RfidTagAssignmentHistoryRepository extends JpaRepository<RfidTagAssignmentHistory, UUID> {

}
