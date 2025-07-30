package de.schaffbar.core_pos.token;

import de.schaffbar.core_pos.CustomerId;
import jakarta.validation.constraints.NotNull;

public interface TokenAssignmentCommands {

    record RequestTokenAssignmentCommand( //
            @NotNull CustomerId customerId, //
            @NotNull TokenType tokenType //
    ) {}

}
