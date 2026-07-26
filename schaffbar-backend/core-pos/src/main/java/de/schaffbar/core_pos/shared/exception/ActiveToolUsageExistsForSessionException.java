package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.WorkshopSessionId;

public class ActiveToolUsageExistsForSessionException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Active tool usage exists for workshop session [id: %s]";

    public ActiveToolUsageExistsForSessionException(WorkshopSessionId workshopSessionId) {
        super(String.format(MESSAGE, workshopSessionId.getValue()), null);
    }

}
