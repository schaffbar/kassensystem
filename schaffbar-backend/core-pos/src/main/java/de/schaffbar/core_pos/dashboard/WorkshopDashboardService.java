package de.schaffbar.core_pos.dashboard;

import static java.util.Comparator.comparing;

import java.util.List;

import de.schaffbar.core_pos.customer.CustomerService;
import de.schaffbar.core_pos.customer.CustomerViews.CustomerView;
import de.schaffbar.core_pos.dashboard.WorkshopDashboardViews.WorkshopDashboardEntryView;
import de.schaffbar.core_pos.workshop_usage.WorkshopUsageService;
import de.schaffbar.core_pos.workshop_usage.WorkshopUsageViews.WorkshopUsageView;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class WorkshopDashboardService {

    private final @NonNull WorkshopUsageService workshopUsageService;

    private final @NonNull CustomerService customerService;

    // ------------------------------------------------------------------------
    // query

    public List<WorkshopDashboardEntryView> getActiveWorkshopUsers() {
        return this.workshopUsageService.getAllActiveWorkshopUsages().stream() //
                .map(this::toWorkshopDashboardEntryView) //
                .sorted(comparing(WorkshopDashboardEntryView::entryTime)) //
                .toList();
    }

    // ------------------------------------------------------------------------
    // helper

    private WorkshopDashboardEntryView toWorkshopDashboardEntryView(WorkshopUsageView usage) {
        CustomerView customer = this.customerService.getCustomer(usage.customerId()) //
                .orElseThrow(() -> new RuntimeException("Customer not found [id: " + usage.customerId() + "]"));

        return new WorkshopDashboardEntryView( //
                customer.id(), //
                customer.firstName(), //
                customer.lastName(), //
                usage.entryTime() //
        );
    }

}
