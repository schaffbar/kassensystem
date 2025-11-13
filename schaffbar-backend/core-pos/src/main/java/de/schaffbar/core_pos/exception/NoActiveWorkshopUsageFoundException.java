package de.schaffbar.core_pos.exception;

import java.io.Serial;

import de.schaffbar.core_pos.id.CustomerId;

public class NoActiveWorkshopUsageFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "No active workshop usage found for customer [id: %s]";

    public NoActiveWorkshopUsageFoundException(CustomerId id) {
        super(String.format(MESSAGE, id.getValue()), null);
    }

}
