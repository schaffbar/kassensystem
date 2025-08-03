package de.schaffbar.core_pos.rfid_tag;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RfidTagRepository extends JpaRepository<RfidTag, UUID> {

}
