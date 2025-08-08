package de.schaffbar.core_pos.rfid_reader;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface RfidReaderRepository extends JpaRepository<RfidReader, UUID> {

    Optional<RfidReader> findByMacAddress(String macAddress);

}
