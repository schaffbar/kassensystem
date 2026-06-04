package de.schaffbar.core_pos.shared.exception;

import java.io.Serial;

import de.schaffbar.core_pos.shared.id.MacAddress;
import de.schaffbar.core_pos.shared.id.RfidReaderId;

public class MacAddressAlreadyUsedException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "MAC address '%s' is already used by RFID reader [id: %s]";

    public MacAddressAlreadyUsedException(MacAddress macAddress, RfidReaderId rfidReaderId) {
        super(String.format(MESSAGE, macAddress.getValue(), rfidReaderId.getValue()), null);
    }

}
