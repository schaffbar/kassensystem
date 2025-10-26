package de.schaffbar.core_pos.customer;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.customer.CustomerCommands.CreateCustomerCommand;
import de.schaffbar.core_pos.customer.CustomerCommands.UpdateCustomerAddressCommand;
import de.schaffbar.core_pos.customer.CustomerCommands.UpdateCustomerCommand;
import de.schaffbar.core_pos.customer.CustomerCommands.UpdateCustomerContactCommand;
import de.schaffbar.core_pos.customer.CustomerViews.CustomerView;
import de.schaffbar.core_pos.id.CustomerId;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class CustomerService {

    private final @NonNull CustomerRepository customerRepository;

    // ------------------------------------------------------------------------
    // query

    public List<CustomerView> getCustomers() {
        return this.customerRepository.findAll().stream() //
                .map(CustomerViewMapper.MAPPER::toCustomerView) //
                .toList();
    }

    public Optional<CustomerView> getCustomer(@NotNull @Valid CustomerId id) {
        return this.customerRepository.findById(id.getValue()) //
                .map(CustomerViewMapper.MAPPER::toCustomerView);
    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public CustomerId createCustomer(@NotNull @Valid CreateCustomerCommand command) {
        Customer customer = Customer.of(command);
        Customer savedCustomer = this.customerRepository.save(customer);

        return savedCustomer.getCustomerId();
    }

    @Transactional
    public void updateCustomer(@NotNull @Valid UpdateCustomerCommand command) {
        this.customerRepository.findById(command.id().getValue()) //
                .ifPresentOrElse( //
                        customer -> customer.update(command), //
                        throwCustomerNotFoundException(command.id()) //
                );
    }

    @Transactional
    public void updateCustomerContact(@NotNull @Valid UpdateCustomerContactCommand command) {
        this.customerRepository.findById(command.id().getValue()) //
                .ifPresentOrElse( //
                        customer -> customer.updateContact(command), //
                        throwCustomerNotFoundException(command.id()) //
                );
    }

    @Transactional
    public void updateCustomerAddress(@NotNull @Valid UpdateCustomerAddressCommand command) {
        this.customerRepository.findById(command.id().getValue()) //
                .ifPresentOrElse( //
                        customer -> customer.updateAddress(command), //
                        throwCustomerNotFoundException(command.id()) //
                );
    }

    @Transactional
    public void deleteCustomer(@NotNull @Valid CustomerId id) {
        CustomerView customerView = getCustomer(id) //
                .orElseThrow(() -> ResourceNotFoundException.customer(id));

        // TODO: Deletion concept needed
        // TODO: check if customer is assigned to a RFID tag before deleting, ...

        this.customerRepository.deleteById(customerView.id().getValue());
    }

    // ------------------------------------------------------------------------
    // helper

    private Runnable throwCustomerNotFoundException(CustomerId id) {
        return () -> {
            throw ResourceNotFoundException.customer(id);
        };
    }

}
