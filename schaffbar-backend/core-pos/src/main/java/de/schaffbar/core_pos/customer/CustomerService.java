package de.schaffbar.core_pos.customer;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.customer.CustomerCommands.CreateCustomerCommand;
import de.schaffbar.core_pos.customer.CustomerViews.CustomerView;
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

    public CustomerId createCustomer(@NotNull @Valid CreateCustomerCommand command) {
        Customer customer = Customer.of(command);
        Customer savedCustomer = this.customerRepository.save(customer);

        return savedCustomer.getCustomerId();
    }

    public void deleteCustomer(@NotNull @Valid CustomerId id) {
        CustomerView customerView = getCustomer(id) //
                .orElseThrow(() -> ResourceNotFoundException.customer(id));

        this.customerRepository.deleteById(customerView.id().getValue());
    }

}
