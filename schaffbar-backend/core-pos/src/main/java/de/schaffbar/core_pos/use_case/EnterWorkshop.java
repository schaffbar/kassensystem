package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.id.CustomerId;
import de.schaffbar.core_pos.id.WorkshopSessionId;
import de.schaffbar.core_pos.workshop_session.WorkshopSessionService;
import de.schaffbar.core_pos.workshop_session.WorkshopSessionViews.WorkshopSessionView;
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
public class EnterWorkshop {

    private final @NonNull WorkshopUsageService workshopUsageService;

    private final @NonNull WorkshopSessionService workshopSessionService;

    @Transactional
    public void process(@NotNull @Valid CustomerId customerId) {

        // TODO: check if customer has a valid certificate to enter the workshop

        WorkshopSessionId sessionId = this.workshopSessionService.getOpenWorkshopSession(customerId) //
                .map(WorkshopSessionView::id) //
                .orElseGet(() -> createWorkshopSession(customerId));

        this.workshopUsageService.enterWorkshop(customerId, sessionId);
    }

    private WorkshopSessionId createWorkshopSession(CustomerId customerId) {
        this.workshopSessionService.startSession(customerId);

        return this.workshopSessionService.getOpenWorkshopSession(customerId) //
                .map(WorkshopSessionView::id) //
                .orElseThrow(() -> new RuntimeException("TODO: Error during creation of workshop session")); // TODO: fix it
    }

}
