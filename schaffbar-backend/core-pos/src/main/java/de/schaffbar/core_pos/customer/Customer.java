package de.schaffbar.core_pos.customer;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import de.schaffbar.core_pos.customer.CustomerCommands.CreateCustomerCommand;
import de.schaffbar.core_pos.customer.CustomerCommands.UpdateCustomerAddressCommand;
import de.schaffbar.core_pos.customer.CustomerCommands.UpdateCustomerCommand;
import de.schaffbar.core_pos.customer.CustomerCommands.UpdateCustomerContactCommand;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.id.CustomerId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "CUSTOMER", schema = "SCHAFFBAR")
class Customer {

    @Id
    private UUID id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private LocalDate dateOfBirth;

    private boolean clubMember;

    @NotBlank
    private String email;

    private String phone;

    @NotNull
    private CustomerAddress address;

    @NotNull
    private Instant createdAt;

    @Version
    private Instant updatedAt;

    // ------------------------------------------------------------------------
    // constructor

    static CustomerWithEvents of(CreateCustomerCommand command) {
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());
        customer.setFirstName(command.firstName());
        customer.setLastName(command.lastName());
        customer.setDateOfBirth(command.dateOfBirth());
        customer.setClubMember(command.clubMember());
        customer.setEmail(command.email());
        customer.setPhone(command.phone());
        customer.setAddress(CustomerAddress.of(command));
        customer.setCreatedAt(Instant.now());

        List<SchaffbarEvent> events = List.of(CustomerEventFactory.customerCreated(customer));

        return new CustomerWithEvents(customer, events);
    }

    // ------------------------------------------------------------------------
    // query

    public CustomerId getCustomerId() {
        return CustomerId.of(this.id);
    }

    // ------------------------------------------------------------------------
    // command

    public List<SchaffbarEvent> update(UpdateCustomerCommand command) {
        this.firstName = command.firstName();
        this.lastName = command.lastName();
        this.dateOfBirth = command.dateOfBirth();
        this.clubMember = command.clubMember();

        return List.of(CustomerEventFactory.customerUpdated(this));
    }

    public List<SchaffbarEvent> updateAddress(UpdateCustomerAddressCommand command) {
        this.address.update(command);

        return List.of(CustomerEventFactory.customerAddressChanged(this));
    }

    public List<SchaffbarEvent> updateContact(UpdateCustomerContactCommand command) {
        this.email = command.email();
        this.phone = command.phone();

        return List.of(CustomerEventFactory.customerContactChanged(this));
    }

    // ------------------------------------------------------------------------
    // helper

    record CustomerWithEvents(Customer customer, List<SchaffbarEvent> events) {}

}
