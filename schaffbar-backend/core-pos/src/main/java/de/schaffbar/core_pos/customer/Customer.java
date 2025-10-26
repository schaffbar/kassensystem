package de.schaffbar.core_pos.customer;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import de.schaffbar.core_pos.customer.CustomerCommands.CreateCustomerCommand;
import de.schaffbar.core_pos.customer.CustomerCommands.UpdateCustomerAddressCommand;
import de.schaffbar.core_pos.customer.CustomerCommands.UpdateCustomerCommand;
import de.schaffbar.core_pos.customer.CustomerCommands.UpdateCustomerContactCommand;
import de.schaffbar.core_pos.id.CustomerId;
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

    static Customer of(CreateCustomerCommand command) {
        Customer customer = new Customer();
        customer.id = UUID.randomUUID();
        customer.firstName = command.firstName();
        customer.lastName = command.lastName();
        customer.dateOfBirth = command.dateOfBirth();
        customer.email = command.email();
        customer.phone = command.phone();
        customer.address = CustomerAddress.of(command);
        customer.createdAt = Instant.now();

        return customer;
    }

    // ------------------------------------------------------------------------
    // query

    public CustomerId getCustomerId() {
        return CustomerId.of(this.id);
    }

    // ------------------------------------------------------------------------
    // command

    public void update(UpdateCustomerCommand command) {
        this.firstName = command.firstName();
        this.lastName = command.lastName();
        this.dateOfBirth = command.dateOfBirth();
    }

    public void updateAddress(UpdateCustomerAddressCommand command) {
        this.address.update(command);
    }

    public void updateContact(UpdateCustomerContactCommand command) {
        this.email = command.email();
        this.phone = command.phone();
    }

}
