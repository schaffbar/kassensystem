package de.schaffbar.core_pos.workshop_usage;

import static java.util.Comparator.comparing;

import java.util.List;
import java.util.Optional;

import de.schaffbar.core_pos.exception.NoActiveWorkshopUsageFoundException;
import de.schaffbar.core_pos.exception.UserAlreadyInWorkshopException;
import de.schaffbar.core_pos.id.CustomerId;
import de.schaffbar.core_pos.id.WorkshopSessionId;
import de.schaffbar.core_pos.workshop_usage.WorkshopUsageViews.WorkshopUsageView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class WorkshopUsageService {

    private final @NonNull WorkshopUsageRepository workshopUsageRepository;

    // ------------------------------------------------------------------------
    // query

    public List<WorkshopUsageView> getWorkshopUsages(@NotNull @Valid WorkshopSessionId workshopSessionId) {
        return this.workshopUsageRepository.findWorkshopUsages(workshopSessionId).stream() //
                .map(WorkshopUsageViews.MAPPER::toWorkshopUsageView) //
                .sorted(comparing(WorkshopUsageView::entryTime)) //
                .toList();
    }

    //    public Optional<WorkshopUsageView> getLatestWorkshopUsage(@NotNull @Valid CustomerId customerId) {
    //        return this.workshopUsageRepository.findLatestByCustomerId(customerId) //
    //                .map(WorkshopUsageViews.MAPPER::toWorkshopUsageView);
    //    }

    // ------------------------------------------------------------------------
    // command

    @Transactional
    public void enterWorkshop(@NotNull @Valid CustomerId customerId, @NotNull @Valid WorkshopSessionId workshopSessionId) {
        getOpenWorkshopUsage(customerId, workshopSessionId) //
                .ifPresent(this::throwCustomerIsAlreadyInWorkshop);

        WorkshopUsage workshopUsage = WorkshopUsage.of(customerId, workshopSessionId);
        this.workshopUsageRepository.save(workshopUsage);
    }

    @Transactional
    public void leaveWorkshop(@NotNull @Valid CustomerId customerId, @NotNull @Valid WorkshopSessionId workshopSessionId) {
        getOpenWorkshopUsage(customerId, workshopSessionId) //
                .ifPresentOrElse( //
                        WorkshopUsage::exit, //
                        throwNoActiveWorkshopUsageFound(customerId));
    }

    // ------------------------------------------------------------------------
    // helper

    // TODO: decide what to use customerId, workshopSessionId or both
    private Optional<WorkshopUsage> getOpenWorkshopUsage(CustomerId customerId, WorkshopSessionId workshopSessionId) {
        return this.workshopUsageRepository.findActiveByCustomerId(customerId);
    }

    private void throwCustomerIsAlreadyInWorkshop(WorkshopUsage workshopUsage) {
        throw new UserAlreadyInWorkshopException(workshopUsage.getCustomerId());
    }

    private Runnable throwNoActiveWorkshopUsageFound(CustomerId customerId) {
        return () -> {
            throw new NoActiveWorkshopUsageFoundException(customerId);
        };
    }

}
