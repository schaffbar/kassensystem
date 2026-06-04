package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.domain.customer.CustomerCommands.CreateCustomerCommand;
import de.schaffbar.core_pos.domain.customer.CustomerService;
import de.schaffbar.core_pos.shared.id.CustomerId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class CustomerCreate {

    private final @NonNull CustomerService customerService;

    @Transactional
    public CustomerId process(@NotNull @Valid CreateCustomerCommand command) {
        return this.customerService.createCustomer(command);
    }

}
