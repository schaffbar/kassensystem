package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.customer.CustomerService;
import de.schaffbar.core_pos.customer.CustomerViews.CustomerView;
import de.schaffbar.core_pos.id.CustomerId;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentService;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentStatus;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentViews.RfidTagAssignmentView;
import de.schaffbar.core_pos.rfid_tag_assignment_history.RfidTagAssignmentHistoryService;
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
public class CustomerUnassignRfidTag {

    private final @NonNull CustomerService customerService;

    private final @NonNull RfidTagAssignmentService rfidTagAssignmentService;

    private final @NonNull RfidTagAssignmentHistoryService rfidTagAssignmentHistoryService;

    @Transactional
    public void process(@NotNull @Valid CustomerId customerId) {
        CustomerView customer = this.customerService.getCustomer(customerId) //
                .orElseThrow(() -> ResourceNotFoundException.customer(customerId));

        RfidTagAssignmentView assignment = this.rfidTagAssignmentService.getRfidTagAssignment(customerId) //
                .filter(a -> a.status() == RfidTagAssignmentStatus.ASSIGNED) //
                .orElseThrow(() -> new RuntimeException("No RFID tag assigned to customer [id: " + customerId.getValue() + "]"));

        this.rfidTagAssignmentHistoryService.moveToHistory(assignment);

        this.rfidTagAssignmentService.unassignRfidTag(customer.id());
    }

}
