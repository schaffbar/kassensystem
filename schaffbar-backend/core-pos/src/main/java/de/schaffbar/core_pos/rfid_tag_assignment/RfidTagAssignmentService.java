package de.schaffbar.core_pos.rfid_tag_assignment;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.exception.NoWaitingAssingmentException;
import de.schaffbar.core_pos.id.CustomerId;
import de.schaffbar.core_pos.id.RfidTagId;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentViews.RfidTagAssignmentView;
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
public class RfidTagAssignmentService {

    private final @NonNull RfidTagAssignmentRepository rfidTagAssignmentRepository;

    // ------------------------------------------------------------------------
    // query

    public List<RfidTagAssignmentView> getRfidTagAssignments() {
        return this.rfidTagAssignmentRepository.findAll().stream() //
                .map(RfidTagAssignmentViewMapper.MAPPER::toRfidTagAssignmentView) //
                .toList();
    }

    public Optional<RfidTagAssignmentView> getRfidTagAssignment(@NotNull @Valid CustomerId customerId) {
        return this.rfidTagAssignmentRepository.findByCustomer(customerId) //
                .map(RfidTagAssignmentViewMapper.MAPPER::toRfidTagAssignmentView);
    }

    public Optional<RfidTagAssignmentView> getRfidTagAssignment(@NotNull @Valid RfidTagId rfidTagId) {
        return this.rfidTagAssignmentRepository.findByRfidTag(rfidTagId) //
                .map(RfidTagAssignmentViewMapper.MAPPER::toRfidTagAssignmentView);
    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public void requestRfidTagAssignment(@NotNull @Valid CustomerId customerId, @NotNull RfidTagAssignmentType assignmentType) {
        if (getRfidTagAssignment(customerId).isPresent()) {
            throw new RuntimeException("RFID tag is already assigned to customer [id: " + customerId.getValue() + "]");
        }

        if (this.rfidTagAssignmentRepository.findWaitingForAssignment().isPresent()) {
            throw new RuntimeException("There is already a RFID tag assignment pending");
        }

        RfidTagAssignment rfidTagAssignment = RfidTagAssignment.of(customerId, assignmentType);
        this.rfidTagAssignmentRepository.save(rfidTagAssignment);
    }

    @Transactional
    public void assignRfidTag(@NotNull @Valid RfidTagId rfidTagId) {
        if (getRfidTagAssignment(rfidTagId).isPresent()) {
            throw new RuntimeException("RFID tag [id: " + rfidTagId.getValue() + "] already assigned");
        }

        this.rfidTagAssignmentRepository.findWaitingForAssignment() //
                .ifPresentOrElse( //
                        assignment -> assignment.assignRfidTag(rfidTagId), //
                        throwNoWaitingAssignmentFound(rfidTagId));
    }

    @Transactional
    public void unassignRfidTag(@NotNull @Valid CustomerId customerId) {
        this.rfidTagAssignmentRepository.findByCustomer(customerId) //
                .filter(RfidTagAssignment::isAssigned) //
                .ifPresentOrElse( //
                        this.rfidTagAssignmentRepository::delete, //
                        throwNoRfidTagAssignedToCustomer(customerId));
    }

    // ------------------------------------------------------------------------
    // helper

    private Runnable throwNoWaitingAssignmentFound(RfidTagId rfidTagId) {
        return () -> {
            throw new NoWaitingAssingmentException(rfidTagId);
        };
    }

    private Runnable throwNoRfidTagAssignedToCustomer(CustomerId customerId) {
        return () -> {
            throw new RuntimeException("No RFID tag assigned to customer [id: " + customerId.getValue() + "]");
        };
    }

}
