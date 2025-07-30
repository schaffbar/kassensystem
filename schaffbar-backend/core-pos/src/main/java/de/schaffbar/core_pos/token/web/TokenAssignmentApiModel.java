package de.schaffbar.core_pos.token.web;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.token.TokenAssignmentStatus;
import de.schaffbar.core_pos.token.TokenType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface TokenAssignmentApiModel {

    // ------------------------------------------------------------------------
    // response

    record TokenAssignmentApiDto( //
            @NotNull UUID id, //
            @NotNull UUID customerId, //
            @NotNull TokenType tokenType, //
            String tokenId, //
            Instant assignmentDate, //
            Instant unassignmentDate, //
            @NotNull TokenAssignmentStatus status //
    ) {}

    // ------------------------------------------------------------------------
    // request body

    record RequestTokenAssignmentRequestBody( //
            @NotNull UUID customerId, //
            @NotNull TokenType tokenType //
    ) {}

    record AssignTokenRequestBody( //
            @NotBlank String tokenId //
    ) {}

}
