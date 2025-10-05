package de.schaffbar.core_pos.workshop_session.web;

import static java.util.Objects.isNull;

import java.util.List;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.workshop_session.WorkshopSessionService;
import de.schaffbar.core_pos.workshop_session.WorkshopSessionViews.WorkshopSessionView;
import de.schaffbar.core_pos.workshop_session.web.WorkshopSessionApiModel.WorkshopSessionApiDto;
import de.schaffbar.core_pos.workshop_usage.WorkshopUsageService;
import de.schaffbar.core_pos.workshop_usage.WorkshopUsageViews.WorkshopUsageView;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/workshop-sessions")
public class WorkshopSessionController {

    private final @NonNull WorkshopSessionService workshopSessionService;

    private final @NonNull WorkshopUsageService workshopUsageService;

    // ------------------------------------------------------------------------
    // query

    @GetMapping(value = "open/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkshopSessionApiDto> getOpenWorkshopSession(@PathVariable @NotNull CustomerId customerId) {
        WorkshopSessionView openSession = this.workshopSessionService.getOpenWorkshopSession(customerId) //
                .orElse(null);

        if (isNull(openSession)) {
            return ResponseEntity.ok(null);
        }

        List<WorkshopUsageView> usages = this.workshopUsageService.getWorkshopUsages(openSession.id());

        WorkshopSessionApiDto result = WorkshopSessionApiMapper.MAPPER.toWorkshopSessionApiDto(openSession, usages);

        return ResponseEntity.ok(result);
    }

    // ------------------------------------------------------------------------
    // command

}
