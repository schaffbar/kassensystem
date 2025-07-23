package de.schaffbar.core_pos.customer.view;

import java.time.Instant;

import de.schaffbar.core_pos.CustomerId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class CustomerView {

    @NotNull
    CustomerId id;

    @NotBlank
    String firstName;

    @NotBlank
    String lastName;

    @NotBlank
    String email;

    @NotBlank
    String phone;

    @NotNull
    CustomerAddressView address;

    @NotNull
    Instant createdAt;

    @NotNull
    Instant updatedAt;

}
