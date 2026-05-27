package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.IpAddress;
import de.schaffbar.core_pos.shared.id.ToolId;

public class IpAddressAlreadyUsedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "IP address '%s' is already used by tool [id: %s]";

    public IpAddressAlreadyUsedException(IpAddress ipAddress, ToolId toolId) {
        super(String.format(MESSAGE, ipAddress.getValue(), toolId.getValue()), null);
    }

}
