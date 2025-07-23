package de.schaffbar.core_pos.customer.web;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.customer.CustomerService;
import de.schaffbar.core_pos.customer.command.CreateCustomerCommand;
import de.schaffbar.core_pos.customer.web.CustomerApiMapper.CreateCustomerRequestBody;
import de.schaffbar.core_pos.customer.web.CustomerApiMapper.CustomerApiDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/customers")
public class CustomerController {

    private final @NonNull CustomerService customerService;

    // ------------------------------------------------------------------------
    // query

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CustomerApiDto>> getAllCustomers() {
        List<CustomerApiDto> customers = this.customerService.getCustomers().stream() //
                .map(CustomerApiMapper.MAPPER::toCustomerApiDto) //
                .toList();

        return ResponseEntity.ok(customers);
    }

    @GetMapping(value = "/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerApiDto> getCustomer(@PathVariable @NotNull UUID customerId) {
        CustomerId customerIdObj = CustomerId.of(customerId);
        CustomerApiDto customer = this.customerService.getCustomer(customerIdObj) //
                .map(CustomerApiMapper.MAPPER::toCustomerApiDto) //
                .orElseThrow(() -> ResourceNotFoundException.customer(customerIdObj));

        return ResponseEntity.ok(customer);
    }

    // ------------------------------------------------------------------------
    // command

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createCustomer(@RequestBody @NotNull @Valid CreateCustomerRequestBody requestBody) {
        CreateCustomerCommand command = CustomerApiMapper.MAPPER.toCreateCustomerCommand(requestBody);
        CustomerId customerId = this.customerService.createCustomer(command);
        URI location = URI.create("/api/v1/customers/" + customerId.getValue());

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable @NotNull UUID customerId) {
        CustomerId customerIdObj = CustomerId.of(customerId);
        this.customerService.deleteCustomer(customerIdObj);

        return ResponseEntity.noContent().build();
    }

}
