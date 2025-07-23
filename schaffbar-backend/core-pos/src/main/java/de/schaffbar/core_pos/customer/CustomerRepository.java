package de.schaffbar.core_pos.customer;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CustomerRepository extends JpaRepository<Customer, UUID> {

}
