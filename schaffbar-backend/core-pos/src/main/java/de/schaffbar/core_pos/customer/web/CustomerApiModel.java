package de.schaffbar.core_pos.customer.web;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface CustomerApiModel {

    // ------------------------------------------------------------------------
    // response

    // TODO: decide to use String or UUID for id, in other places we use String
    record CustomerApiDto( //
            @NotNull UUID id, //
            @NotBlank String firstName, //
            @NotBlank String lastName, //
            @NotBlank String email, //
            @NotBlank String phone, //
            @NotNull CustomerAddressApiDto address, //
            @NotNull Instant createdAt, //
            @NotNull Instant updatedAt //
    ) {}

    record CustomerAddressApiDto( //
            @NotBlank String addressLine1, //
            String addressLine2, //
            @NotBlank String postalCode, //
            @NotBlank String city, //
            @NotBlank String country //
    ) {}

    // ------------------------------------------------------------------------
    // request body

    record CreateCustomerRequestBody( //
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
