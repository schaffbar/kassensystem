package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;

public class CustomerNotCertifiedForToolException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Customer [id: %s] does not have an active certification for tool [id: %s]";

    public CustomerNotCertifiedForToolException(CustomerId customerId, ToolId toolId) {
        super(String.format(MESSAGE, customerId.getValue(), toolId.getValue()), null);
    }

}
