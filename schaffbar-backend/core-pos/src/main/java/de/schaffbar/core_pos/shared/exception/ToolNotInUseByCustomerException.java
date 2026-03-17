package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;

public class ToolNotInUseByCustomerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "No active tool usage found for customer [id: %s] and tool [id: %s]";

    public ToolNotInUseByCustomerException(CustomerId customerId, ToolId toolId) {
        super(String.format(MESSAGE, customerId.getValue(), toolId.getValue()), null);
    }

}
