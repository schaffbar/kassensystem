package de.schaffbar.core_pos.customer;

import de.schaffbar.core_pos.shared.event.payload.CustomerEventPayload.CustomerAddressChangedPayload;
import de.schaffbar.core_pos.shared.event.payload.CustomerEventPayload.CustomerContactChangedPayload;
import de.schaffbar.core_pos.shared.event.payload.CustomerEventPayload.CustomerCreatedPayload;
import de.schaffbar.core_pos.shared.event.payload.CustomerEventPayload.CustomerUpdatedPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
interface CustomerPayloadMapper {

    CustomerPayloadMapper MAPPER = Mappers.getMapper(CustomerPayloadMapper.class);

    // ------------------------------------------------------------------------
    // mapper

    @Mapping(target = "id", source = "customerId")
    CustomerCreatedPayload toCustomerCreatedPayload(Customer customer);

    @Mapping(target = "id", source = "customerId")
    CustomerUpdatedPayload toCustomerUpdatedPayload(Customer customer);

    @Mapping(target = "id", source = "customerId")
    CustomerContactChangedPayload toCustomerContactChangedPayload(Customer customer);

    @Mapping(target = "id", source = "customerId")
    @Mapping(target = "addressLine1", source = "address.addressLine1")
    @Mapping(target = "addressLine2", source = "address.addressLine2")
    @Mapping(target = "postalCode", source = "address.postalCode")
    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "country", source = "address.country")
    CustomerAddressChangedPayload toCustomerAddressChangedPayload(Customer customer);

}
