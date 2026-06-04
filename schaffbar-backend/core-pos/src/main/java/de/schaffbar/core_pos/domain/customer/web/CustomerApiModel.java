package de.schaffbar.core_pos.domain.customer.web;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;

import de.schaffbar.core_pos.domain.customer.CustomerCommands.CreateCustomerCommand;
import de.schaffbar.core_pos.domain.customer.CustomerCommands.UpdateCustomerAddressCommand;
import de.schaffbar.core_pos.domain.customer.CustomerCommands.UpdateCustomerCommand;
import de.schaffbar.core_pos.domain.customer.CustomerCommands.UpdateCustomerContactCommand;
import de.schaffbar.core_pos.domain.customer.CustomerViews.CustomerAddressView;
import de.schaffbar.core_pos.domain.customer.CustomerViews.CustomerView;
import de.schaffbar.core_pos.shared.id.CustomerId;
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
    CustomerApiDto toCustomerApiDto(CustomerView customer);

    CustomerAddressApiDto toCustomerAddressApiDto(CustomerAddressView address);

    // ------------------------------------------------------------------------
    // mapping request body to command

    CreateCustomerCommand toCreateCustomerCommand(CreateCustomerRequestBody requestBody);

    UpdateCustomerCommand toUpdateCustomerCommand(CustomerId id, UpdateCustomerRequestBody requestBody);

    UpdateCustomerContactCommand toUpdateCustomerContactCommand(CustomerId id, UpdateCustomerContactRequestBody requestBody);

    UpdateCustomerAddressCommand toUpdateCustomerAddressCommand(CustomerId id, UpdateCustomerAddressRequestBody requestBody);

    @Named("toAgeGroup")
    default AgeGroup toAgeGroup(LocalDate dateOfBirth) {
        int ageInYears = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (ageInYears < 0) {
            throw new IllegalArgumentException("Date of birth is in the future: " + dateOfBirth);
        }

        if (ageInYears < 16) {
            return AgeGroup.UNDER_16;
        }
        else if (ageInYears < 18) {
            return AgeGroup.UNDER_18;
        }
        else {
            return AgeGroup.ADULT;
        }

    }

    // ------------------------------------------------------------------------
    // response

    enum AgeGroup {
        UNDER_16, UNDER_18, ADULT
    }

    record CustomerApiDto( //
            @NotBlank String id, //
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
