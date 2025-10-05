package de.schaffbar.core_pos.workshop_session.web;

import java.time.Instant;
import java.util.List;

import de.schaffbar.core_pos.workshop_session.WorkshopSessionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public interface WorkshopSessionApiModel {

    // ------------------------------------------------------------------------
    // response

    record WorkshopSessionApiDto( //
            @NotBlank String id, //
            @NotBlank String customerId, //
            @NotNull Instant startTime, //
            Instant closeTime, //
            WorkshopSessionStatus status, //
            List<WorkshopUsageApiDto> workshopUsages //
    ) {}

    record WorkshopUsageApiDto( //
            @NotNull String id, //
            @NotNull @PastOrPresent Instant entryTime, //
            @PastOrPresent Instant exitTime, //
            Long durationInMinutes //
    ) {}

    // ------------------------------------------------------------------------
    // request body

}
