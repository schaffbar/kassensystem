package de.schaffbar.core_pos.use_case;

import java.util.Objects;

import de.schaffbar.core_pos.domain.customer.CustomerService;
import de.schaffbar.core_pos.domain.customer.CustomerViews.CustomerView;
import de.schaffbar.core_pos.domain.tool_usage.ToolUsageService;
import de.schaffbar.core_pos.domain.tool_usage.ToolUsageViews.ToolUsageView;
import de.schaffbar.core_pos.domain.workshop_session.WorkshopSessionService;
import de.schaffbar.core_pos.domain.workshop_session.WorkshopSessionViews.WorkshopSessionView;
import de.schaffbar.core_pos.domain.workshop_usage.WorkshopUsageService;
import de.schaffbar.core_pos.domain.workshop_usage.WorkshopUsageViews.WorkshopUsageView;
import de.schaffbar.core_pos.shared.exception.ActiveToolUsageExistsForSessionException;
import de.schaffbar.core_pos.shared.exception.ActiveWorkshopUsageExistsForSessionException;
import de.schaffbar.core_pos.shared.exception.NoActiveWorkshopSessionFoundException;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
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
public class CloseSession {

    private final @NonNull CustomerService customerService;

    private final @NonNull WorkshopSessionService workshopSessionService;

    private final @NonNull WorkshopUsageService workshopUsageService;

    private final @NonNull ToolUsageService toolUsageService;

    @Transactional
    public void process(@NotNull @Valid CustomerId customerId) {
        CustomerView customer = this.customerService.getCustomer(customerId) //
                .orElseThrow(() -> ResourceNotFoundException.customer(customerId));

        WorkshopSessionId sessionId = this.workshopSessionService.getOpenWorkshopSession(customer.id()) //
                .map(WorkshopSessionView::id) //
                .orElseThrow(() -> new NoActiveWorkshopSessionFoundException(customer.id()));

        ensureNoActiveToolUsages(sessionId);
        ensureNoActiveWorkshopUsages(sessionId);

        this.workshopSessionService.closeSession(customer.id());
    }

    private void ensureNoActiveWorkshopUsages(WorkshopSessionId sessionId) {
        boolean hasActiveWorkshopUsage = this.workshopUsageService.getWorkshopUsages(sessionId).stream() //
                .map(WorkshopUsageView::exitTime) //
                .anyMatch(Objects::isNull);

        if (hasActiveWorkshopUsage) {
            throw new ActiveWorkshopUsageExistsForSessionException(sessionId);
        }
    }

    private void ensureNoActiveToolUsages(WorkshopSessionId sessionId) {
        boolean hasActiveToolUsage = this.toolUsageService.getToolUsages(sessionId).stream() //
                .map(ToolUsageView::endTime) //
                .anyMatch(Objects::isNull);

        if (hasActiveToolUsage) {
            throw new ActiveToolUsageExistsForSessionException(sessionId);
        }
    }

}
