package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.ToolUsageId;

public class ToolAlreadyStoppedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Tool usage [id: %s] has already been stopped";

    public ToolAlreadyStoppedException(ToolUsageId id) {
        super(String.format(MESSAGE, id.getValue()), null);
    }

}
