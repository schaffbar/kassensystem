package de.schaffbar.core_pos.customer;

import java.time.LocalDate;

import de.schaffbar.core_pos.id.CustomerId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface CustomerCommands {

    record CreateCustomerCommand( //
            @NotBlank String firstName, //
            @NotBlank String lastName, //
            @NotNull LocalDate dateOfBirth, //
            boolean clubMember, //
            @NotBlank String email, //
            String phone, //
            @NotBlank String addressLine1, //
            String addressLine2, //
            @NotBlank String postalCode, //
            @NotBlank String city, //
            @NotBlank String country //
    ) {}

    record UpdateCustomerCommand( //
            @NotNull CustomerId id, //
            @NotBlank String firstName, //
            @NotBlank String lastName, //
            @NotNull LocalDate dateOfBirth, //
            boolean clubMember //
    ) {}

    record UpdateCustomerContactCommand( //
            @NotNull CustomerId id, //
            @NotBlank String email, //
            String phone //
    ) {}

    record UpdateCustomerAddressCommand( //
            @NotNull CustomerId id, //
            @NotBlank String addressLine1, //
            String addressLine2, //
            @NotBlank String postalCode, //
            @NotBlank String city, //
            @NotBlank String country //
    ) {}

}
