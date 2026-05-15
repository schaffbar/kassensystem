package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.ToolId;

public class CertificationAlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Certification already exists for customer [id: %s] and tool [id: %s]";

    public CertificationAlreadyExistsException(CustomerId customerId, ToolId toolId) {
        super(String.format(MESSAGE, customerId.getValue(), toolId.getValue()), null);
    }

}
