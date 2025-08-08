package de.schaffbar.core_pos.rfid_tag_assignment;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.RfidTagId;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentCommands.RequestRfidTagAssignmentCommand;
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
        return rfidTagAssignmentRepository.findAll().stream() //
                .map(RfidTagAssignmentViewMapper.MAPPER::toRfidTagAssignmentView) //
                .toList();
    }

    public Optional<RfidTagAssignmentView> getRfidTagAssignment(@NotNull @Valid CustomerId customerId) {
        return rfidTagAssignmentRepository.findByCustomer(customerId) //
                .map(RfidTagAssignmentViewMapper.MAPPER::toRfidTagAssignmentView);
    }

    public Optional<RfidTagAssignmentView> getRfidTagAssignment(@NotNull @Valid RfidTagId rfidTagId) {
        return rfidTagAssignmentRepository.findByRfidTag(rfidTagId) //
                .map(RfidTagAssignmentViewMapper.MAPPER::toRfidTagAssignmentView);
    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public void requestRfidTagAssignment(@NotNull @Valid RequestRfidTagAssignmentCommand command) {
        // TODO: check if customer exists (move from controller to facade/use case)

        if (getRfidTagAssignment(command.customerId()).isPresent()) {
            throw new RuntimeException("Rfid tag assignment already exists for customer with id: " + command.customerId().getValue());
        }

        if (this.rfidTagAssignmentRepository.findWaitingForAssignment().isPresent()) {
            throw new RuntimeException("There is already a rfid tag assignment pending");
        }

        RfidTagAssignment rfidTagAssignment = RfidTagAssignment.of(command);
        this.rfidTagAssignmentRepository.save(rfidTagAssignment);
    }

    @Transactional
    public void assignRfidTag(@NotNull @Valid RfidTagId rfidTagId) {
        // TODO: check if rfid tag exists

        if (getRfidTagAssignment(rfidTagId).isPresent()) {
            throw new RuntimeException("Rfid tag assignment with rfid tag " + rfidTagId + " already exists");
        }

        this.rfidTagAssignmentRepository.findWaitingForAssignment() //
                .ifPresentOrElse( //
                        assignment -> assignment.assignRfidTag(rfidTagId), //
                        throwNoWaitingAssignmentFound());
    }

    // ------------------------------------------------------------------------
    // helper

    private Runnable throwNoWaitingAssignmentFound() {
        return () -> {
            throw new RuntimeException("No waiting for rfid tag assignment found");
        };
    }

}
