package de.schaffbar.core_pos.customer;

import de.schaffbar.core_pos.customer.CustomerCommands.CreateCustomerCommand;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class CustomerAddress {

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @NotBlank
    private String postalCode;

    @NotBlank
    private String city;

    @NotBlank
    private String country;

    static CustomerAddress of(CreateCustomerCommand command) {
        CustomerAddress address = new CustomerAddress();
        address.addressLine1 = command.addressLine1();
        address.addressLine2 = command.addressLine2();
        address.postalCode = command.postalCode();
        address.city = command.city();
        address.country = command.country();

        return address;
    }

}
