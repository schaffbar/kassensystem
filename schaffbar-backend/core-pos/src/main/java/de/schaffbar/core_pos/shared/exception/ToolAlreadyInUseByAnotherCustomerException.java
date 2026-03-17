package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.ToolId;

public class ToolAlreadyInUseByAnotherCustomerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Tool [id: %s] is already in use by another customer";

    public ToolAlreadyInUseByAnotherCustomerException(ToolId id) {
        super(String.format(MESSAGE, id.getValue()), null);
    }

}
