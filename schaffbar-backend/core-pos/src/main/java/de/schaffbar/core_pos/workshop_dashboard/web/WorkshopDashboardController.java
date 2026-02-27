package de.schaffbar.core_pos.workshop_dashboard.web;

import java.util.List;

import de.schaffbar.core_pos.workshop_dashboard.WorkshopDashboardService;
import de.schaffbar.core_pos.workshop_dashboard.web.WorkshopDashboardApiModel.WorkshopDashboardEntryApiDto;
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
@RequestMapping(path = "/api/v1/workshop-dashboard")
public class WorkshopDashboardController {

    private final @NonNull WorkshopDashboardService workshopDashboardService;

    // ------------------------------------------------------------------------
    // query

    @GetMapping(value = "/active-users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WorkshopDashboardEntryApiDto>> getActiveWorkshopUsers() {
        List<WorkshopDashboardEntryApiDto> activeUsers = this.workshopDashboardService.getActiveWorkshopUsers().stream() //
                .map(WorkshopDashboardApiModel.MAPPER::toWorkshopDashboardEntryApiDto) //
                .toList();

        return ResponseEntity.ok(activeUsers);
    }

}
