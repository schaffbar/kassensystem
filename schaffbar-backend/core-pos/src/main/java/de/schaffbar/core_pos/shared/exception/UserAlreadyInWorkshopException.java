package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.CustomerId;

public class UserAlreadyInWorkshopException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Customer [id: %s] is already in workshop";

    public UserAlreadyInWorkshopException(CustomerId id) {
        super(String.format(MESSAGE, id.getValue()), null);
    }

}
