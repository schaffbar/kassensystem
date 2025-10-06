package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.WorkshopSessionId;
import de.schaffbar.core_pos.workshop_session.WorkshopSessionService;
import de.schaffbar.core_pos.workshop_session.WorkshopSessionViews;
import de.schaffbar.core_pos.workshop_usage.WorkshopUsageService;
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
public class LeaveWorkshop {

    private final @NonNull WorkshopSessionService workshopSessionService;

    private final @NonNull WorkshopUsageService workshopUsageService;

    @Transactional
    public void process(@NotNull @Valid CustomerId customerId) {

        // TODO: stop any active tool usage associated with the given RFID tag, if present

        WorkshopSessionId sessionId = this.workshopSessionService.getOpenWorkshopSession(customerId) //
                .map(WorkshopSessionViews.WorkshopSessionView::id) //
                .orElseThrow(() -> new RuntimeException("TODO: Error during creation of workshop session")); // TODO: fix it

        this.workshopUsageService.leaveWorkshop(customerId, sessionId);
    }

}
