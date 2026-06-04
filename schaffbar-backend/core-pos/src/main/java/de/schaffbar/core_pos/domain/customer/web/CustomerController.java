package de.schaffbar.core_pos.domain.customer.web;

import java.net.URI;
import java.util.List;

import de.schaffbar.core_pos.domain.customer.CustomerCommands.CreateCustomerCommand;
import de.schaffbar.core_pos.domain.customer.CustomerCommands.UpdateCustomerAddressCommand;
import de.schaffbar.core_pos.domain.customer.CustomerCommands.UpdateCustomerCommand;
import de.schaffbar.core_pos.domain.customer.CustomerCommands.UpdateCustomerContactCommand;
import de.schaffbar.core_pos.domain.customer.CustomerService;
import de.schaffbar.core_pos.domain.customer.web.CustomerApiModel.CreateCustomerRequestBody;
import de.schaffbar.core_pos.domain.customer.web.CustomerApiModel.CustomerApiDto;
import de.schaffbar.core_pos.domain.customer.web.CustomerApiModel.UpdateCustomerAddressRequestBody;
import de.schaffbar.core_pos.domain.customer.web.CustomerApiModel.UpdateCustomerContactRequestBody;
import de.schaffbar.core_pos.domain.customer.web.CustomerApiModel.UpdateCustomerRequestBody;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.use_case.CustomerCreate;
import de.schaffbar.core_pos.use_case.CustomerDelete;
import de.schaffbar.core_pos.use_case.CustomerUpdate;
import de.schaffbar.core_pos.use_case.CustomerUpdateAddress;
import de.schaffbar.core_pos.use_case.CustomerUpdateContact;
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

    private final @NonNull CustomerCreate customerCreate;

    private final @NonNull CustomerUpdate customerUpdate;

    private final @NonNull CustomerUpdateContact customerUpdateContact;

    private final @NonNull CustomerUpdateAddress customerUpdateAddress;

    private final @NonNull CustomerDelete customerDelete;

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
    public ResponseEntity<CustomerApiDto> getCustomer(@PathVariable @NotNull CustomerId customerId) {
        CustomerApiDto customer = this.customerService.getCustomer(customerId) //
                .map(CustomerApiModel.MAPPER::toCustomerApiDto) //
                .orElseThrow(() -> ResourceNotFoundException.customer(customerId));

        return ResponseEntity.ok(customer);
    }

    // ------------------------------------------------------------------------
    // command

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createCustomer(@RequestBody @NotNull @Valid CreateCustomerRequestBody requestBody) {
        CreateCustomerCommand command = CustomerApiModel.MAPPER.toCreateCustomerCommand(requestBody);
        CustomerId customerId = this.customerCreate.process(command);
        URI location = URI.create("/api/v1/customers/" + customerId.getValue());

        return ResponseEntity.created(location).build();
    }

    @PutMapping(value = "/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateCustomer(@PathVariable @NotNull CustomerId customerId,
            @RequestBody @NotNull @Valid UpdateCustomerRequestBody requestBody) {
        UpdateCustomerCommand command = CustomerApiModel.MAPPER.toUpdateCustomerCommand(customerId, requestBody);
        this.customerUpdate.process(command);

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{customerId}/contact", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateCustomerContact(@PathVariable @NotNull CustomerId customerId,
            @RequestBody @NotNull @Valid UpdateCustomerContactRequestBody requestBody) {
        UpdateCustomerContactCommand command = CustomerApiModel.MAPPER.toUpdateCustomerContactCommand(customerId, requestBody);
        this.customerUpdateContact.process(command);

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{customerId}/address", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateCustomerAddress(@PathVariable @NotNull CustomerId customerId,
            @RequestBody @NotNull @Valid UpdateCustomerAddressRequestBody requestBody) {
        UpdateCustomerAddressCommand command = CustomerApiModel.MAPPER.toUpdateCustomerAddressCommand(customerId, requestBody);
        this.customerUpdateAddress.process(command);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable @NotNull CustomerId customerId) {
        this.customerDelete.process(customerId);

        return ResponseEntity.noContent().build();
    }

}
