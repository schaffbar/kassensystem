package de.schaffbar.core_pos.domain.customer;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.domain.customer.CustomerCommands.CreateCustomerCommand;
import de.schaffbar.core_pos.domain.customer.CustomerCommands.UpdateCustomerAddressCommand;
import de.schaffbar.core_pos.domain.customer.CustomerCommands.UpdateCustomerCommand;
import de.schaffbar.core_pos.domain.customer.CustomerCommands.UpdateCustomerContactCommand;
import de.schaffbar.core_pos.domain.customer.CustomerViews.CustomerView;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEventRepository;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class CustomerService {

    private final @NonNull CustomerRepository customerRepository;

    private final @NonNull OutboxEventRepository outboxEventRepository;

    // ------------------------------------------------------------------------
    // query

    public List<CustomerView> getCustomers() {
        return this.customerRepository.findAll().stream() //
                .map(CustomerViews.MAPPER::toCustomerView) //
                .toList();
    }

    public Optional<CustomerView> getCustomer(@NotNull @Valid CustomerId id) {
        return this.customerRepository.findById(id.getValue()) //
                .map(CustomerViews.MAPPER::toCustomerView);
    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public CustomerId createCustomer(@NotNull @Valid CreateCustomerCommand command) {
        Customer customer = Customer.of(command);
        Customer savedCustomer = this.customerRepository.save(customer);
        List<SchaffbarEvent> events = List.of(CustomerEventFactory.customerCreated(savedCustomer));

        saveOutboxEvents(events);

        log.info("Created customer with id {} and published events {}", savedCustomer.getId(), events);

        return savedCustomer.getId();
    }

    @Transactional
    public void updateCustomer(@NotNull @Valid UpdateCustomerCommand command) {
        List<SchaffbarEvent> events = this.customerRepository.findById(command.id().getValue()) //
                .map(customer -> customer.update(command)) //
                .orElseThrow(() -> ResourceNotFoundException.customer(command.id()));

        saveOutboxEvents(events);

        log.info("Updated customer with id {} and published events {}", command.id(), events);
    }

    @Transactional
    public void updateCustomerContact(@NotNull @Valid UpdateCustomerContactCommand command) {
        List<SchaffbarEvent> events = this.customerRepository.findById(command.id().getValue()) //
                .map(customer -> customer.updateContact(command)) //
                .orElseThrow(() -> ResourceNotFoundException.customer(command.id()));

        saveOutboxEvents(events);

        log.info("Updated contact of customer with id {} and published events {}", command.id(), events);
    }

    @Transactional
    public void updateCustomerAddress(@NotNull @Valid UpdateCustomerAddressCommand command) {
        List<SchaffbarEvent> events = this.customerRepository.findById(command.id().getValue()) //
                .map(customer -> customer.updateAddress(command)) //
                .orElseThrow(() -> ResourceNotFoundException.customer(command.id()));

        saveOutboxEvents(events);

        log.info("Updated address of customer with id {} and published events {}", command.id(), events);
    }

    @Transactional
    public void deleteCustomer(@NotNull @Valid CustomerId id) {
        CustomerView customerView = getCustomer(id) //
                .orElseThrow(() -> ResourceNotFoundException.customer(id));

        // TODO: Deletion concept needed
        // TODO: check if customer is assigned to a RFID tag before deleting, ...

        this.customerRepository.deleteById(customerView.id().getValue());

        List<SchaffbarEvent> events = List.of(CustomerEventFactory.customerDeleted(id));
        saveOutboxEvents(events);

        log.info("Deleted customer with id {} and published events {}", id, events);
    }

    // ------------------------------------------------------------------------
    // helper

    // TODO: DRY: this method is duplicated in multiple services, maybe move to a common base class or utility class?
    private void saveOutboxEvents(List<SchaffbarEvent> events) {
        List<OutboxEvent> outboxEvents = events.stream() //
                .map(OutboxEvent::of) //
                .toList();

        this.outboxEventRepository.saveAll(outboxEvents);
    }

}
