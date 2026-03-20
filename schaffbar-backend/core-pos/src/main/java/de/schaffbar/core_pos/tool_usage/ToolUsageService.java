package de.schaffbar.core_pos.tool_usage;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEvent;
import de.schaffbar.core_pos.shared.event.outbox.OutboxEventRepository;
import de.schaffbar.core_pos.shared.exception.CustomerAlreadyUsingToolException;
import de.schaffbar.core_pos.shared.exception.MaxToolUsageExceededException;
import de.schaffbar.core_pos.shared.exception.ToolAlreadyInUseByAnotherCustomerException;
import de.schaffbar.core_pos.shared.exception.ToolNotInUseByCustomerException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.shared.id.WorkshopSessionId;
import de.schaffbar.core_pos.tool_usage.ToolUsageCommands.StartToolUsageCommand;
import de.schaffbar.core_pos.tool_usage.ToolUsageViews.ToolUsageView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ToolUsageService {

    static final int MAX_SIMULTANEOUS_TOOL_USAGES = 2;

    private final @NonNull ToolUsageRepository toolUsageRepository;

    private final @NonNull OutboxEventRepository outboxEventRepository;

    // ------------------------------------------------------------------------
    // query

    public List<ToolUsageView> getActiveToolUsages(@NotNull @Valid CustomerId customerId) {
        return this.toolUsageRepository.findActiveByCustomerId(customerId).stream() //
                .map(ToolUsageViews.MAPPER::toToolUsageView) //
                .toList();
    }

    public Optional<ToolUsageView> getActiveToolUsage(@NotNull @Valid CustomerId customerId, @NotNull @Valid ToolId toolId) {
        return this.toolUsageRepository.findActiveByCustomerIdAndToolId(customerId, toolId) //
                .map(ToolUsageViews.MAPPER::toToolUsageView);
    }

    public List<ToolUsageView> getToolUsages(@NotNull @Valid WorkshopSessionId workshopSessionId) {
        return this.toolUsageRepository.findByWorkshopSessionId(workshopSessionId).stream() //
                .map(ToolUsageViews.MAPPER::toToolUsageView) //
                .toList();
    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public void startUsage(@NotNull @Valid StartToolUsageCommand command) {
        ensureToolNotInUseByAnotherCustomer(command.toolId(), command.customerId());
        ensureCustomerNotAlreadyUsingTool(command.customerId(), command.toolId());
        ensureMaxSimultaneousUsagesNotExceeded(command.customerId());

        ToolUsage toolUsage = ToolUsage.of(command);
        this.toolUsageRepository.save(toolUsage);

        List<SchaffbarEvent> events = List.of(ToolUsageEventFactory.toolUsageStarted(toolUsage));
        saveOutboxEvents(events);

        log.info("Started tool usage for customer {} on tool {} and published events {}", command.customerId(), command.toolId(), events);
    }

    @Transactional
    public void stopUsage(@NotNull @Valid CustomerId customerId, @NotNull @Valid ToolId toolId) {
        ToolUsage usage = this.toolUsageRepository.findActiveByCustomerIdAndToolId(customerId, toolId) //
                .orElseThrow(() -> new ToolNotInUseByCustomerException(customerId, toolId));

        List<SchaffbarEvent> events = usage.stop();
        saveOutboxEvents(events);

        log.info("Stopped tool usage for customer {} on tool {} and published events {}", customerId, toolId, events);
    }

    @Transactional
    public void stopAllUsagesForCustomer(@NotNull @Valid CustomerId customerId) {
        List<ToolUsage> activeUsages = this.toolUsageRepository.findActiveByCustomerId(customerId);
        if (activeUsages.isEmpty()) {
            log.info("No active tool usages found for customer {}. Nothing to stop.", customerId);
            return;
        }

        List<SchaffbarEvent> events = activeUsages.stream() //
                .map(ToolUsage::stop) //
                .flatMap(List::stream) //
                .toList();

        saveOutboxEvents(events);

        log.info("Stopped all tool usages for customer {} and published events {}", customerId, events);
    }

    // ------------------------------------------------------------------------
    // helper

    private void saveOutboxEvents(List<SchaffbarEvent> events) {
        List<OutboxEvent> outboxEvents = events.stream() //
                .map(OutboxEvent::of) //
                .toList();

        this.outboxEventRepository.saveAll(outboxEvents);
    }

    private void ensureToolNotInUseByAnotherCustomer(ToolId toolId, CustomerId customerId) {
        this.toolUsageRepository.findActiveByToolId(toolId) //
                .filter(existing -> !existing.getCustomerId().sameValueAs(customerId)) //
                .ifPresent(existing -> {
                    throw new ToolAlreadyInUseByAnotherCustomerException(toolId);
                });
    }

    private void ensureCustomerNotAlreadyUsingTool(CustomerId customerId, ToolId toolId) {
        this.toolUsageRepository.findActiveByCustomerIdAndToolId(customerId, toolId) //
                .ifPresent(existing -> {
                    throw new CustomerAlreadyUsingToolException(customerId, toolId);
                });
    }

    private void ensureMaxSimultaneousUsagesNotExceeded(CustomerId customerId) {
        long activeCount = this.toolUsageRepository.countActiveByCustomerId(customerId);
        if (activeCount >= MAX_SIMULTANEOUS_TOOL_USAGES) {
            throw new MaxToolUsageExceededException(customerId, MAX_SIMULTANEOUS_TOOL_USAGES);
        }
    }

}
