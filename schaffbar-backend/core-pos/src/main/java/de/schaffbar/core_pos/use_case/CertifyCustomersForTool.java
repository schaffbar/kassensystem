package de.schaffbar.core_pos.use_case;

import java.util.ArrayList;
import java.util.List;

import de.schaffbar.core_pos.customer.CustomerService;
import de.schaffbar.core_pos.shared.exception.CustomerNotInstructorException;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool.ToolService;
import de.schaffbar.core_pos.tool_certification.BatchCertificationResult;
import de.schaffbar.core_pos.tool_certification.BatchCertificationResult.BatchCertificationError;
import de.schaffbar.core_pos.tool_certification.ToolCertificationCommands.CreateToolCertificationCommand;
import de.schaffbar.core_pos.tool_certification.ToolCertificationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class CertifyCustomersForTool {

    private final @NonNull ToolCertificationService toolCertificationService;

    private final @NonNull CustomerService customerService;

    private final @NonNull ToolService toolService;

    public BatchCertificationResult process( //
            @NotEmpty List<@Valid @NotNull CustomerId> customerIds, //
            @NotNull @Valid ToolId toolId, //
            @NotNull @Valid CustomerId certifiedBy //
    ) {
        verifyToolExists(toolId);
        verifyCertifiedByIsInstructor(certifiedBy, toolId);

        List<CustomerId> succeeded = new ArrayList<>();
        List<BatchCertificationError> failed = new ArrayList<>();

        for (CustomerId customerId : customerIds) {
            try {
                verifyCustomerExists(customerId);
                CreateToolCertificationCommand command = new CreateToolCertificationCommand(customerId, toolId, certifiedBy);
                this.toolCertificationService.create(command);
                succeeded.add(customerId);
            }
            catch (Exception e) {
                log.warn("Failed to create certification for customer {} on tool {}: {}", customerId, toolId, e.getMessage());
                failed.add(new BatchCertificationError(customerId, e.getMessage()));
            }
        }

        log.info("Batch certification for tool {}: {} succeeded, {} failed", toolId, succeeded.size(), failed.size());

        return new BatchCertificationResult(succeeded, failed);
    }

    // ------------------------------------------------------------------------
    // helper

    private void verifyToolExists(ToolId toolId) {
        this.toolService.getTool(toolId) //
                .orElseThrow(() -> ResourceNotFoundException.tool(toolId));
    }

    private void verifyCustomerExists(CustomerId customerId) {
        this.customerService.getCustomer(customerId) //
                .orElseThrow(() -> ResourceNotFoundException.customer(customerId));
    }

    private void verifyCertifiedByIsInstructor(CustomerId certifiedBy, ToolId toolId) {
        this.customerService.getCustomer(certifiedBy) //
                .orElseThrow(() -> ResourceNotFoundException.customer(certifiedBy));

        if (!this.toolService.isInstructor(toolId, certifiedBy)) {
            throw new CustomerNotInstructorException(certifiedBy, toolId);
        }
    }

}
