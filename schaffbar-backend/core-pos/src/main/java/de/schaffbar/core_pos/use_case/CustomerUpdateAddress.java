package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.domain.customer.CustomerCommands.UpdateCustomerAddressCommand;
import de.schaffbar.core_pos.domain.customer.CustomerService;
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
public class CustomerUpdateAddress {

    private final @NonNull CustomerService customerService;

    @Transactional
    public void process(@NotNull @Valid UpdateCustomerAddressCommand command) {
        this.customerService.updateCustomerAddress(command);
    }

}
