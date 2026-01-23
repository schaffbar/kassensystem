package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.rfid_tag.RfidTagService;
import de.schaffbar.core_pos.rfid_tag.RfidTagViews.RfidTagView;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentService;
import de.schaffbar.core_pos.shared.id.RfidTagId;
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
public class CustomerAssignRfidTag {

    private final @NonNull RfidTagService rfidTagService;

    private final @NonNull RfidTagAssignmentService rfidTagAssignmentService;

    @Transactional
    public void process(@NotNull @Valid RfidTagId rfidTagId) {
        RfidTagView rfidTag = this.rfidTagService.getRfidTag(rfidTagId) //
                .orElseThrow(() -> ResourceNotFoundException.rfidTag(rfidTagId));

        this.rfidTagAssignmentService.assignRfidTag(rfidTag.id());
    }

}
