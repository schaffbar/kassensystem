package de.schaffbar.core_pos.domain.customer;

import de.schaffbar.core_pos.shared.event.EventType;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.event.payload.CustomerEventPayload.CustomerDeletedPayload;
import de.schaffbar.core_pos.shared.id.CustomerId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class CustomerEventFactory {

    static SchaffbarEvent customerCreated(Customer customer) {
        var payload = CustomerPayloadMapper.MAPPER.toCustomerCreatedPayload(customer);
        payload.validate();

        return SchaffbarEvent.customerEvent(EventType.CUSTOMER_CREATED, customer.getCustomerId(), payload);
    }

    static SchaffbarEvent customerUpdated(Customer customer) {
        var payload = CustomerPayloadMapper.MAPPER.toCustomerUpdatedPayload(customer);
        payload.validate();

        return SchaffbarEvent.customerEvent(EventType.CUSTOMER_UPDATED, customer.getCustomerId(), payload);
    }

    static SchaffbarEvent customerContactChanged(Customer customer) {
        var payload = CustomerPayloadMapper.MAPPER.toCustomerContactChangedPayload(customer);
        payload.validate();

        return SchaffbarEvent.customerEvent(EventType.CUSTOMER_CONTACT_CHANGED, customer.getCustomerId(), payload);
    }

    static SchaffbarEvent customerAddressChanged(Customer customer) {
        var payload = CustomerPayloadMapper.MAPPER.toCustomerAddressChangedPayload(customer);
        payload.validate();

        return SchaffbarEvent.customerEvent(EventType.CUSTOMER_ADDRESS_CHANGED, customer.getCustomerId(), payload);
    }

    static SchaffbarEvent customerDeleted(CustomerId customerId) {
        var payload = new CustomerDeletedPayload(customerId);
        payload.validate();

        return SchaffbarEvent.customerEvent(EventType.CUSTOMER_DELETED, customerId, payload);
    }

}
