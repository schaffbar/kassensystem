package de.schaffbar.core_pos.customer;

import java.time.Instant;
import java.time.LocalDate;

import de.schaffbar.core_pos.id.CustomerId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CustomerViews {

    CustomerViews MAPPER = Mappers.getMapper(CustomerViews.class);

    // ------------------------------------------------------------------------
    // mapper

    @Mapping(target = "id", source = "customerId")
    CustomerView toCustomerView(Customer customer);

    // ------------------------------------------------------------------------
    // views

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
    ) {

        public String getFullName() {
            return this.firstName + " " + this.lastName;
        }

    }

    record CustomerAddressView( //
            @NotBlank String addressLine1, //
            String addressLine2, //
            @NotBlank String postalCode, //
            @NotBlank String city, //
            @NotBlank String country //
    ) {}

}
