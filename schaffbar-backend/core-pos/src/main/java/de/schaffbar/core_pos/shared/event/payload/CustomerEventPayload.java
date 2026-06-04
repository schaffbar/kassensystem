package de.schaffbar.core_pos.shared.event.payload;

import java.time.Instant;
import java.time.LocalDate;

import de.schaffbar.core_pos.domain.customer.CustomerAddress;
import de.schaffbar.core_pos.shared.event.EventPayload;
import de.schaffbar.core_pos.shared.id.CustomerId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public interface CustomerEventPayload extends EventPayload {

    record CustomerCreatedPayload( //
            @Valid @NotNull CustomerId id, //
            @NotBlank String firstName, //
            @NotBlank String lastName, //
            @NotNull LocalDate dateOfBirth, //
            boolean clubMember, //
            @NotBlank String email, //
            String phone, //
            @NotNull CustomerAddress address, //
            @NotNull @PastOrPresent Instant createdAt //
    ) implements CustomerEventPayload {}

    record CustomerUpdatedPayload( //
            @Valid @NotNull CustomerId id, //
            @NotBlank String firstName, //
            @NotBlank String lastName, //
            @NotNull LocalDate dateOfBirth, //
            boolean clubMember //
    ) implements CustomerEventPayload {}

    record CustomerContactChangedPayload( //
            @Valid @NotNull CustomerId id, //
            @NotBlank String email, //
            String phone //
    ) implements CustomerEventPayload {}

    record CustomerAddressChangedPayload( //
            @Valid @NotNull CustomerId id, //
            @NotBlank String addressLine1, //
            String addressLine2, //
            @NotBlank String postalCode, //
            @NotBlank String city, //
            @NotBlank String country //
    ) implements CustomerEventPayload {}

    record CustomerDeletedPayload( //
            @Valid @NotNull CustomerId id //
    ) implements CustomerEventPayload {}

}
