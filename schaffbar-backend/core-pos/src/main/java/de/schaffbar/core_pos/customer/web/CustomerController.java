package de.schaffbar.core_pos.customer.web;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.customer.CustomerCommands.CreateCustomerCommand;
import de.schaffbar.core_pos.customer.CustomerCommands.UpdateCustomerAddressCommand;
import de.schaffbar.core_pos.customer.CustomerCommands.UpdateCustomerCommand;
import de.schaffbar.core_pos.customer.CustomerCommands.UpdateCustomerContactCommand;
import de.schaffbar.core_pos.customer.CustomerService;
import de.schaffbar.core_pos.customer.web.CustomerApiModel.CreateCustomerRequestBody;
import de.schaffbar.core_pos.customer.web.CustomerApiModel.CustomerApiDto;
import de.schaffbar.core_pos.customer.web.CustomerApiModel.UpdateCustomerAddressRequestBody;
import de.schaffbar.core_pos.customer.web.CustomerApiModel.UpdateCustomerContactRequestBody;
import de.schaffbar.core_pos.customer.web.CustomerApiModel.UpdateCustomerRequestBody;
import de.schaffbar.core_pos.shared.id.CustomerId;
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
import org.springframework.web.bind.annotation.PutMapping;
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
                .map(CustomerApiModel.MAPPER::toCustomerApiDto) //
                .toList();

        return ResponseEntity.ok(customers);
    }

    @GetMapping(value = "/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerApiDto> getCustomer(@PathVariable @NotNull UUID customerId) {
        CustomerId id = CustomerId.of(customerId);
        CustomerApiDto customer = this.customerService.getCustomer(id) //
                .map(CustomerApiModel.MAPPER::toCustomerApiDto) //
                .orElseThrow(() -> ResourceNotFoundException.customer(id));

        return ResponseEntity.ok(customer);
    }

    // ------------------------------------------------------------------------
    // command

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createCustomer(@RequestBody @NotNull @Valid CreateCustomerRequestBody requestBody) {
        CreateCustomerCommand command = CustomerApiModel.MAPPER.toCreateCustomerCommand(requestBody);
        CustomerId customerId = this.customerService.createCustomer(command);
        URI location = URI.create("/api/v1/customers/" + customerId.getValue());

        return ResponseEntity.created(location).build();
    }

    @PutMapping(value = "/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateCustomer(@PathVariable @NotNull CustomerId customerId,
            @RequestBody @NotNull @Valid UpdateCustomerRequestBody requestBody) {
        UpdateCustomerCommand command = CustomerApiModel.MAPPER.toUpdateCustomerCommand(customerId, requestBody);
        this.customerService.updateCustomer(command);

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{customerId}/contact", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateCustomerContact(@PathVariable @NotNull CustomerId customerId,
            @RequestBody @NotNull @Valid UpdateCustomerContactRequestBody requestBody) {
        UpdateCustomerContactCommand command = CustomerApiModel.MAPPER.toUpdateCustomerContactCommand(customerId, requestBody);
        this.customerService.updateCustomerContact(command);

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{customerId}/address", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateCustomerAddress(@PathVariable @NotNull CustomerId customerId,
            @RequestBody @NotNull @Valid UpdateCustomerAddressRequestBody requestBody) {
        UpdateCustomerAddressCommand command = CustomerApiModel.MAPPER.toUpdateCustomerAddressCommand(customerId, requestBody);
        this.customerService.updateCustomerAddress(command);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable @NotNull CustomerId customerId) {
        this.customerService.deleteCustomer(customerId);

        return ResponseEntity.noContent().build();
    }

}
