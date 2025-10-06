package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.customer.CustomerService;
import de.schaffbar.core_pos.customer.CustomerViews.CustomerView;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentService;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentType;
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
public class CustomerRequestRfidTagAssignment {

    private final @NonNull CustomerService customerService;

    private final @NonNull RfidTagAssignmentService rfidTagAssignmentService;

    @Transactional
    public void process(@NotNull @Valid CustomerId customerId, @NotNull RfidTagAssignmentType assignmentType) {
        CustomerView customer = this.customerService.getCustomer(customerId) //
                .orElseThrow(() -> ResourceNotFoundException.customer(customerId));

        this.rfidTagAssignmentService.requestRfidTagAssignment(customer.id(), assignmentType);
    }

}
