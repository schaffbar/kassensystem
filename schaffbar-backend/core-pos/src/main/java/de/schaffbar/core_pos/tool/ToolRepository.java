package de.schaffbar.core_pos.tool;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ToolRepository extends JpaRepository<Tool, UUID> {

}
