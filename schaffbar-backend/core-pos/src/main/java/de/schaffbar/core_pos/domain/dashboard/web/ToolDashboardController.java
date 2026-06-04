package de.schaffbar.core_pos.domain.dashboard.web;

import java.util.List;

import de.schaffbar.core_pos.domain.dashboard.ToolDashboardService;
import de.schaffbar.core_pos.domain.dashboard.web.ToolDashboardApiModel.ToolDashboardEntryApiDto;
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
@RequestMapping(path = "/api/v1/tool-dashboard")
public class ToolDashboardController {

    private final @NonNull ToolDashboardService toolDashboardService;

    // ------------------------------------------------------------------------
    // query

    @GetMapping(value = "/active-tools", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ToolDashboardEntryApiDto>> getActiveToolUsages() {
        List<ToolDashboardEntryApiDto> activeToolUsages = this.toolDashboardService.getActiveToolUsages().stream() //
                .map(ToolDashboardApiModel.MAPPER::toToolDashboardEntryApiDto) //
                .toList();

        return ResponseEntity.ok(activeToolUsages);
    }

}
