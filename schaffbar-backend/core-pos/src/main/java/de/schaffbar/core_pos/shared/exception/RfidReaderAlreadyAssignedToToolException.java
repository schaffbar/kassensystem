package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ToolId;

public class RfidReaderAlreadyAssignedToToolException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "RFID reader [id: %s] is already assigned to tool [id: %s]";

    public RfidReaderAlreadyAssignedToToolException(RfidReaderId rfidReaderId, ToolId toolId) {
        super(String.format(MESSAGE, rfidReaderId.getValue(), toolId.getValue()), null);
    }

}
