package de.schaffbar.core_pos.customer;

import jakarta.validation.constraints.NotBlank;

public interface CustomerCommands {

    record CreateCustomerCommand( //
            @NotBlank String firstName, //
            @NotBlank String lastName, //
            @NotBlank String email, //
            @NotBlank String phone, //
            @NotBlank String addressLine1, //
            String addressLine2, //
            @NotBlank String postalCode, //
            @NotBlank String city, //
            @NotBlank String country //
    ) {}

}
