package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.RfidReaderId;

public class InvalidRfidReaderTypeForToolException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "RFID reader [id: %s] has an invalid type for tool assignment. Only SWITCH_BOX readers can be assigned to tools.";

    public InvalidRfidReaderTypeForToolException(RfidReaderId rfidReaderId) {
        super(String.format(MESSAGE, rfidReaderId.getValue()), null);
    }

}
