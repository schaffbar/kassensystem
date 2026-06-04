package de.schaffbar.core_pos.use_case;

import de.schaffbar.core_pos.domain.tool_usage.ToolUsageService;
import de.schaffbar.core_pos.domain.workshop_session.WorkshopSessionService;
import de.schaffbar.core_pos.domain.workshop_session.WorkshopSessionViews;
import de.schaffbar.core_pos.domain.workshop_usage.WorkshopUsageService;
import de.schaffbar.core_pos.shared.exception.NoActiveWorkshopSessionFoundException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.WorkshopSessionId;
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

    private final @NonNull ToolUsageService toolUsageService;

    @Transactional
    public void process(@NotNull @Valid CustomerId customerId) {

        // Stop any active tool usages for the customer before leaving the workshop
        this.toolUsageService.stopAllUsagesForCustomer(customerId);

        WorkshopSessionId sessionId = this.workshopSessionService.getOpenWorkshopSession(customerId) //
                .map(WorkshopSessionViews.WorkshopSessionView::id) //
                .orElseThrow(() -> new NoActiveWorkshopSessionFoundException(customerId));

        this.workshopUsageService.leaveWorkshop(customerId, sessionId);
    }

}
