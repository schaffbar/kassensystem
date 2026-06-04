package de.schaffbar.core_pos.domain.rfid_tag_assignment_history.web;

import java.util.List;

import de.schaffbar.core_pos.domain.rfid_tag_assignment_history.RfidTagAssignmentHistoryService;
import de.schaffbar.core_pos.domain.rfid_tag_assignment_history.web.RfidTagAssignmentHistoryApiModel.RfidTagAssignmentHistoryApiDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/rfid-tag-assignment-history")
public class RfidTagAssignmentHistoryController {

    private final @NonNull RfidTagAssignmentHistoryService rfidTagAssignmentHistoryService;

    // ------------------------------------------------------------------------
    // query

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RfidTagAssignmentHistoryApiDto>> getRfidTagAssignmentHistory() {
        List<RfidTagAssignmentHistoryApiDto> result = this.rfidTagAssignmentHistoryService.getRfidTagAssignmentHistory().stream() //
                .map(RfidTagAssignmentHistoryApiModel.MAPPER::toRfidTagAssignmentHistoryApiDto) //
                .toList();

        return ResponseEntity.ok(result);
    }

    // ------------------------------------------------------------------------
    // command

}
