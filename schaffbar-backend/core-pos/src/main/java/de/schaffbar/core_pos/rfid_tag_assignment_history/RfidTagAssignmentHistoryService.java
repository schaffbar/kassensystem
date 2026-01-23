package de.schaffbar.core_pos.rfid_tag_assignment_history;

import java.util.List;

import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentViews.RfidTagAssignmentView;
import de.schaffbar.core_pos.rfid_tag_assignment_history.RfidTagAssignmentHistoryViews.RfidTagAssignmentHistoryView;
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
public class RfidTagAssignmentHistoryService {

    private final @NonNull RfidTagAssignmentHistoryRepository rfidTagAssignmentHistoryRepository;

    // ------------------------------------------------------------------------
    // query

    public List<RfidTagAssignmentHistoryView> getRfidTagAssignmentHistory() {
        return this.rfidTagAssignmentHistoryRepository.findAll().stream() //
                .map(RfidTagAssignmentHistoryViews.MAPPER::toRfidTagAssignmentView) //
                .toList();
    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public void moveToHistory(@NotNull @Valid RfidTagAssignmentView activeAssignment) {
        // TODO: check preconditions -> activeAssignment.status == ASSIGNED and other

        RfidTagAssignmentHistory rfidTagAssignment = RfidTagAssignmentHistory.of(activeAssignment);
        this.rfidTagAssignmentHistoryRepository.save(rfidTagAssignment);
    }

}
