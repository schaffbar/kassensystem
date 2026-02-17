package de.schaffbar.core_pos.shared.id;

import static java.util.Objects.isNull;

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

    public boolean sameValueAs(MacAddress other) {
        if (isNull(other)) {
            return false;
        }

        return this.value.equals(other.value);
    }

}
