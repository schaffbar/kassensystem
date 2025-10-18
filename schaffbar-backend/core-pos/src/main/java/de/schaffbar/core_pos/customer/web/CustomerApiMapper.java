package de.schaffbar.core_pos.customer.web;

import de.schaffbar.core_pos.customer.CustomerCommands.CreateCustomerCommand;
import de.schaffbar.core_pos.customer.CustomerCommands.UpdateCustomerAddressCommand;
import de.schaffbar.core_pos.customer.CustomerCommands.UpdateCustomerContactCommand;
import de.schaffbar.core_pos.customer.CustomerViews.CustomerAddressView;
import de.schaffbar.core_pos.customer.CustomerViews.CustomerView;
import de.schaffbar.core_pos.customer.web.CustomerApiModel.CreateCustomerRequestBody;
import de.schaffbar.core_pos.customer.web.CustomerApiModel.CustomerAddressApiDto;
import de.schaffbar.core_pos.customer.web.CustomerApiModel.CustomerApiDto;
import de.schaffbar.core_pos.customer.web.CustomerApiModel.UpdateCustomerAddressRequestBody;
import de.schaffbar.core_pos.customer.web.CustomerApiModel.UpdateCustomerContactRequestBody;
import de.schaffbar.core_pos.id.CustomerId;
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

    UpdateCustomerContactCommand toUpdateCustomerContactCommand(CustomerId id, UpdateCustomerContactRequestBody requestBody);

    UpdateCustomerAddressCommand toUpdateCustomerAddressCommand(CustomerId id, UpdateCustomerAddressRequestBody requestBody);

}
