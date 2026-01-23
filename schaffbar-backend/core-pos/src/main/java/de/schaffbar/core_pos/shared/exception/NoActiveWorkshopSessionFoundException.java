package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.CustomerId;

public class NoActiveWorkshopSessionFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "No active workshop session found for customer [id: %s]";

    public NoActiveWorkshopSessionFoundException(CustomerId id) {
        super(String.format(MESSAGE, id.getValue()), null);
    }

}
