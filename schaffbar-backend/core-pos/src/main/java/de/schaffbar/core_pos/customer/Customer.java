package de.schaffbar.core_pos.customer;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.customer.command.CreateCustomerCommand;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
//@Table(name = "RES_PARTNER", schema = "PUBLIC")
@Table(name = "CUSTOMER", schema = "SCHAFFBAR")
class Customer {

    @Id
    private UUID id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String email;

    @NotBlank
    private String phone;

    @NotNull
    private CustomerAddress address;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    // ------------------------------------------------------------------------
    // constructor

    static Customer of(CreateCustomerCommand command) {
        Customer customer = new Customer();
        customer.id = UUID.randomUUID();
        customer.firstName = command.getFirstName();
        customer.email = command.getEmail();
        customer.phone = command.getPhone();
        customer.lastName = command.getLastName();
        customer.address = CustomerAddress.of(command);
        customer.createdAt = Instant.now();
        customer.updatedAt = Instant.now();

        return customer;
    }

    // ------------------------------------------------------------------------
    // query

    public CustomerId getCustomerId() {
        return CustomerId.of(this.id);
    }

    // ------------------------------------------------------------------------
    // command

}
