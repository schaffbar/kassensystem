package de.schaffbar.core_pos.domain.dashboard;

import java.util.List;

import de.schaffbar.core_pos.domain.customer.CustomerService;
import de.schaffbar.core_pos.domain.customer.CustomerViews.CustomerView;
import de.schaffbar.core_pos.domain.dashboard.ToolDashboardViews.ToolDashboardEntryView;
import de.schaffbar.core_pos.domain.tool.ToolService;
import de.schaffbar.core_pos.domain.tool.ToolViews.ToolView;
import de.schaffbar.core_pos.domain.tool_usage.ToolUsageService;
import de.schaffbar.core_pos.domain.tool_usage.ToolUsageViews.ToolUsageView;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class ToolDashboardService {

    private final @NonNull ToolUsageService toolUsageService;

    private final @NonNull ToolService toolService;

    private final @NonNull CustomerService customerService;

    // ------------------------------------------------------------------------
    // query

    public List<ToolDashboardEntryView> getActiveToolUsages() {
        return this.toolUsageService.getAllActiveToolUsages().stream() //
                .map(this::toToolDashboardEntryView) //
                .toList();
    }

    // ------------------------------------------------------------------------
    // helper

    private ToolDashboardEntryView toToolDashboardEntryView(ToolUsageView usage) {
        ToolView tool = this.toolService.getTool(usage.toolId()) //
                .orElseThrow(() -> ResourceNotFoundException.tool(usage.toolId()));

        CustomerView customer = this.customerService.getCustomer(usage.customerId()) //
                .orElseThrow(() -> ResourceNotFoundException.customer(usage.customerId()));

        return ToolDashboardEntryView.of(tool, customer);
    }

}
