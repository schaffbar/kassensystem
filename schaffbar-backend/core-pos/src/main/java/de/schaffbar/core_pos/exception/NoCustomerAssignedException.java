package de.schaffbar.core_pos.exception;

import java.io.Serial;

import de.schaffbar.core_pos.id.RfidTagId;

public class NoCustomerAssignedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "No customer assigned to RFID tag [id: %s]";

    public NoCustomerAssignedException(RfidTagId id) {
        super(String.format(MESSAGE, id.getValue()), null);
    }

}
