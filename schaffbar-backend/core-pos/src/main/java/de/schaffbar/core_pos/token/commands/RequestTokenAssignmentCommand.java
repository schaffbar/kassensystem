package de.schaffbar.core_pos.token.commands;

import de.schaffbar.core_pos.CustomerId;
import de.schaffbar.core_pos.token.TokenType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RequestTokenAssignmentCommand {

    CustomerId customerId;

    TokenType tokenType;

}
