package de.schaffbar.core_pos.customer.web;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import de.schaffbar.core_pos.customer.CustomerCommands;
import de.schaffbar.core_pos.customer.CustomerViews;
import de.schaffbar.core_pos.id.CustomerId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CustomerApiModel {

    CustomerApiModel MAPPER = Mappers.getMapper(CustomerApiModel.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "ageGroup", source = "dateOfBirth", qualifiedByName = "toAgeGroup")
    CustomerApiDto toCustomerApiDto(CustomerViews.CustomerView customer);

    CustomerAddressApiDto toCustomerAddressApiDto(CustomerViews.CustomerAddressView address);

    // ------------------------------------------------------------------------
    // mapping request body to command

    CustomerCommands.CreateCustomerCommand toCreateCustomerCommand(CreateCustomerRequestBody requestBody);

    CustomerCommands.UpdateCustomerCommand toUpdateCustomerCommand(CustomerId id, UpdateCustomerRequestBody requestBody);

    CustomerCommands.UpdateCustomerContactCommand toUpdateCustomerContactCommand(CustomerId id, UpdateCustomerContactRequestBody requestBody);

    CustomerCommands.UpdateCustomerAddressCommand toUpdateCustomerAddressCommand(CustomerId id, UpdateCustomerAddressRequestBody requestBody);

    @Named("toAgeGroup")
    default CustomerApiModel.AgeGroup toAgeGroup(LocalDate dateOfBirth) {
        int ageInYears = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (ageInYears < 0) {
            throw new IllegalArgumentException("Date of birth is in the future: " + dateOfBirth);
        }

        if (ageInYears < 16) {
            return CustomerApiModel.AgeGroup.UNDER_16;
        }
        else if (ageInYears < 18) {
            return CustomerApiModel.AgeGroup.UNDER_18;
        }
        else {
            return CustomerApiModel.AgeGroup.ADULT;
        }

    }

    // ------------------------------------------------------------------------
    // response

    enum AgeGroup {
        UNDER_16, UNDER_18, ADULT
    }

    // TODO: decide to use String or UUID for id, in other places we use String
    record CustomerApiDto( //
            @NotNull UUID id, //
            @NotBlank String firstName, //
            @NotBlank String lastName, //
            @NotBlank String dateOfBirth, //
            @NotNull AgeGroup ageGroup, //
            boolean clubMember, //
            @NotBlank String email, //
            String phone, //
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

    record UpdateCustomerRequestBody( //
            @NotBlank String firstName, //
            @NotBlank String lastName, //
            @NotNull LocalDate dateOfBirth, //
            boolean clubMember //
    ) {}

    record UpdateCustomerAddressRequestBody( //
            @NotBlank String addressLine1, //
            String addressLine2, //
            @NotBlank String postalCode, //
            @NotBlank String city, //
            @NotBlank String country //
    ) {}

    record UpdateCustomerContactRequestBody( //
            @NotBlank String email, //
            String phone //
    ) {}

}
