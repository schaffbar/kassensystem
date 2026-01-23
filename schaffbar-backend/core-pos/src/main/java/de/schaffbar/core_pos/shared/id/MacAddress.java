package de.schaffbar.core_pos.shared.id;

import de.schaffbar.core_pos.shared.id.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class MacAddress {

    String value;

    private MacAddress(String macAddress) {
        ValueObjectAssert.notBlank(macAddress, ValueObject.MAC_ADDRESS);
        // TODO: validate MAC address format
        this.value = macAddress;
    }

}
