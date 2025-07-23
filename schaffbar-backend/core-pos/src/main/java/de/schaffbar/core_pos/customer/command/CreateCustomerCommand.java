package de.schaffbar.core_pos.customer.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateCustomerCommand {

    @NotBlank
    String firstName;

    @NotBlank
    String lastName;

    @NotBlank
    String email;

    @NotBlank
    String phone;

    @NotBlank
    String addressLine1;

    String addressLine2;

    @NotBlank
    String postalCode;

    @NotBlank
    String city;

    @NotBlank
    String country;

}
