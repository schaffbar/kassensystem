package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.RfidReaderId;

public class RfidReaderHasToolAssignedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Cannot change type of RFID reader [id: %s] because it is currently assigned to a tool. Please unassign the tool first before changing the type.";

    public RfidReaderHasToolAssignedException(RfidReaderId id) {
        super(String.format(MESSAGE, id.getValue()), null);
    }

}
