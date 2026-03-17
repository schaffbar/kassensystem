package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.CustomerId;

public class MaxToolUsageExceededException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Customer [id: %s] has reached the maximum number of simultaneously used tools (%d)";

    public MaxToolUsageExceededException(CustomerId id, int maxToolUsages) {
        super(String.format(MESSAGE, id.getValue(), maxToolUsages), null);
    }

}
