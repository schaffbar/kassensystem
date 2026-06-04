package de.schaffbar.core_pos.domain.workshop_session;

import java.time.Instant;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.WorkshopSessionId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface WorkshopSessionViews {

    WorkshopSessionViews MAPPER = Mappers.getMapper(WorkshopSessionViews.class);

    // ------------------------------------------------------------------------
    // mapper

    WorkshopSessionView toWorkshopSessionView(WorkshopSession workshopSession);

    // ------------------------------------------------------------------------
    // views

    record WorkshopSessionView( //
            @NotNull WorkshopSessionId id, //
            @NotNull CustomerId customerId, //
            @NotNull @PastOrPresent Instant startTime, //
            @PastOrPresent Instant closeTime, //
            @NotNull WorkshopSessionStatus status //
    ) {}

}
