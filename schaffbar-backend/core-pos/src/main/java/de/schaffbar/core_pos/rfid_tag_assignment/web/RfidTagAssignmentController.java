package de.schaffbar.core_pos.rfid_tag_assignment.web;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import java.util.UUID;

import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.customer.CustomerService;
import de.schaffbar.core_pos.customer.CustomerViews.CustomerView;
import de.schaffbar.core_pos.id.CustomerId;
import de.schaffbar.core_pos.id.RfidTagId;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentService;
import de.schaffbar.core_pos.rfid_tag_assignment.web.RfidTagAssignmentApiModel.AssignRfidTagRequestBody;
import de.schaffbar.core_pos.rfid_tag_assignment.web.RfidTagAssignmentApiModel.RequestRfidTagAssignmentRequestBody;
import de.schaffbar.core_pos.rfid_tag_assignment.web.RfidTagAssignmentApiModel.RfidTagAssignmentApiDto;
import de.schaffbar.core_pos.rfid_tag_assignment.web.RfidTagAssignmentApiModel.UnassignRfidTagRequestBody;
import de.schaffbar.core_pos.use_case.CustomerAssignRfidTag;
import de.schaffbar.core_pos.use_case.CustomerRequestRfidTagAssignment;
import de.schaffbar.core_pos.use_case.CustomerUnassignRfidTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/rfid-tag-assignments")
public class RfidTagAssignmentController {

    private final @NonNull RfidTagAssignmentService rfidTagAssignmentService;

    private final @NonNull CustomerService customerService;

    private final @NonNull CustomerRequestRfidTagAssignment requestRfidTagAssignmentUseCase;

    private final @NonNull CustomerAssignRfidTag assignRfidTagUseCase;

    private final @NonNull CustomerUnassignRfidTag unassignRfidTagUseCase;

    // ------------------------------------------------------------------------
    // query

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RfidTagAssignmentApiDto>> getAllRfidTagAssignments( //
            @RequestParam(value = "rfid-tag-id", required = false) String rfidTagId, //
            @RequestParam(value = "customer-id", required = false) String customerId) {

        List<RfidTagAssignmentApiDto> result;
        if (isNotBlank(rfidTagId)) {
            result = this.rfidTagAssignmentService.getRfidTagAssignment(RfidTagId.of(rfidTagId)).stream() //
                    .map(RfidTagAssignmentApiMapper.MAPPER::toRfidTagAssignmentApiDto) //
                    .toList();
        }
        else if (isNotBlank(customerId)) {
            CustomerId customerIdObj = CustomerId.of(UUID.fromString(customerId));
            CustomerView customer = this.customerService.getCustomer(customerIdObj) //
                    .orElseThrow(() -> ResourceNotFoundException.customer(customerIdObj));

            result = this.rfidTagAssignmentService.getRfidTagAssignment(customer.id()).stream() //
                    .map(RfidTagAssignmentApiMapper.MAPPER::toRfidTagAssignmentApiDto) //
                    .toList();
        }
        else {
            result = this.rfidTagAssignmentService.getRfidTagAssignments().stream() //
                    .map(RfidTagAssignmentApiMapper.MAPPER::toRfidTagAssignmentApiDto) //
                    .toList();
        }

        return ResponseEntity.ok(result);
    }

    // ------------------------------------------------------------------------
    // command

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> requestRfidTagAssignment(@RequestBody @Valid @NotNull RequestRfidTagAssignmentRequestBody requestBody) {
        CustomerId customerId = CustomerId.of(requestBody.customerId());
        this.requestRfidTagAssignmentUseCase.process(customerId, requestBody.assignmentType());

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/assign", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> assignRfidTag(@RequestBody @Valid @NotNull AssignRfidTagRequestBody requestBody) {
        RfidTagId rfidTagId = RfidTagId.of(requestBody.rfidTagId());
        this.assignRfidTagUseCase.process(rfidTagId);

        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/unassign", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> assignRfidTag(@RequestBody @Valid @NotNull UnassignRfidTagRequestBody requestBody) {
        CustomerId customerId = CustomerId.of(requestBody.customerId());
        this.unassignRfidTagUseCase.process(customerId);

        return ResponseEntity.ok().build();
    }

}
