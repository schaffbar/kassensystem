package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.RfidTagId;

public class NoWaitingAssingmentException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "RFID nicht zugewiesen"; // TODO: improve message

    public NoWaitingAssingmentException(RfidTagId id) {
        super(String.format(MESSAGE, id.getValue()), null);
    }

}
