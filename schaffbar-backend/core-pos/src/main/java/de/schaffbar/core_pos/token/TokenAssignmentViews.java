package de.schaffbar.core_pos.token;

import java.time.Instant;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.TokenAssignmentId;
import de.schaffbar.core_pos.TokenId;
import jakarta.validation.constraints.NotNull;

public interface TokenAssignmentViews {

    record TokenAssignmentView( //
            @NotNull TokenAssignmentId id, //
            @NotNull CustomerId customerId, //
            @NotNull TokenType tokenType, //
            TokenId tokenId, //
            Instant assignmentDate, //
            Instant unassignmentDate, //
            @NotNull TokenAssignmentStatus status //
    ) {}

}
