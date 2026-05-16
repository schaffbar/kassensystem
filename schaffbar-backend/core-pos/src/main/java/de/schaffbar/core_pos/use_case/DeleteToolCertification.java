package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.customer.CustomerService;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool.ToolService;
import de.schaffbar.core_pos.tool_certification.ToolCertificationService;
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
public class DeleteToolCertification {

    private final @NonNull ToolCertificationService toolCertificationService;

    private final @NonNull CustomerService customerService;

    private final @NonNull ToolService toolService;

    @Transactional
    public void process(@NotNull @Valid CustomerId customerId, @NotNull @Valid ToolId toolId) {
        verifyCustomerExists(customerId);
        verifyToolExists(toolId);

        this.toolCertificationService.delete(customerId, toolId);
    }

    // ------------------------------------------------------------------------
    // helper

    private void verifyCustomerExists(CustomerId customerId) {
        this.customerService.getCustomer(customerId) //
                .orElseThrow(() -> ResourceNotFoundException.customer(customerId));
    }

    private void verifyToolExists(ToolId toolId) {
        this.toolService.getTool(toolId) //
                .orElseThrow(() -> ResourceNotFoundException.tool(toolId));
    }

}
