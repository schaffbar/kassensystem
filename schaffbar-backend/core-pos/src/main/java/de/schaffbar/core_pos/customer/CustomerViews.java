package de.schaffbar.core_pos.customer;

import java.time.Instant;
import java.time.LocalDate;

import de.schaffbar.core_pos.id.CustomerId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface CustomerViews {

    record CustomerView( //
            @NotNull CustomerId id, //
            @NotBlank String firstName, //
            @NotBlank String lastName, //
            @NotNull LocalDate dateOfBirth, //
            boolean clubMember, //
            @NotBlank String email, //
            String phone, //
            @NotNull CustomerAddressView address, //
            @NotNull Instant createdAt, //
            @NotNull Instant updatedAt //
    ) {}

    record CustomerAddressView( //
            @NotBlank String addressLine1, //
            String addressLine2, //
            @NotBlank String postalCode, //
            @NotBlank String city, //
            @NotBlank String country //
    ) {}

}
