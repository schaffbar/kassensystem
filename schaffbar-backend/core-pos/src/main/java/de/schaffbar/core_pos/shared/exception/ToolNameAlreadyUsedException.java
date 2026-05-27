package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.ToolId;

public class ToolNameAlreadyUsedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Tool name '%s' is already used by tool [id: %s]";

    public ToolNameAlreadyUsedException(String name, ToolId toolId) {
        super(String.format(MESSAGE, name, toolId.getValue()), null);
    }

}
