package de.schaffbar.core_pos.customer.web;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.customer.command.CreateCustomerCommand;
import de.schaffbar.core_pos.customer.view.CustomerAddressView;
import de.schaffbar.core_pos.customer.view.CustomerView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CustomerApiMapper {

    CustomerApiMapper MAPPER = Mappers.getMapper(CustomerApiMapper.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "id", source = "id.value")
    CustomerApiDto toCustomerApiDto(CustomerView customer);

    CustomerAddressApiDto toCustomerAddressApiDto(CustomerAddressView address);

    // ------------------------------------------------------------------------
    // mapping request body to command

    CreateCustomerCommand toCreateCustomerCommand(CreateCustomerRequestBody requestBody);

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

    // ------------------------------------------------------------------------
    // response dto

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

}
