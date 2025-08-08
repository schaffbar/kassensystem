package de.schaffbar.core_pos.rfid_tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RfidTagRepository extends JpaRepository<RfidTag, String> {

}
