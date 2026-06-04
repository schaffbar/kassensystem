package de.schaffbar.core_pos.use_case;

import java.util.List;

import de.schaffbar.core_pos.domain.customer.CustomerService;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.domain.tool.ToolService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class ToolRemoveInstructors {

    private final @NonNull ToolService toolService;

    private final @NonNull CustomerService customerService;

    @Transactional
    public void process(@NotNull @Valid ToolId toolId, @NotEmpty List<@NotNull @Valid CustomerId> instructorIds) {
        instructorIds.forEach(this::verifyCustomerExists);

        this.toolService.removeInstructors(toolId, instructorIds);
    }

    // ------------------------------------------------------------------------
    // helper

    private void verifyCustomerExists(CustomerId customerId) {
        this.customerService.getCustomer(customerId) //
                .orElseThrow(() -> ResourceNotFoundException.customer(customerId));
    }

}
