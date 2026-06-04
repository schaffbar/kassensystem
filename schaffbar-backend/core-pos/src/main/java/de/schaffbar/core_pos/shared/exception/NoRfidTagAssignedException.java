package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.CustomerId;

public class NoRfidTagAssignedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "No RFID tag assigned to customer [id: %s]";

    public NoRfidTagAssignedException(CustomerId customerId) {
        super(String.format(MESSAGE, customerId.getValue()), null);
    }

}
