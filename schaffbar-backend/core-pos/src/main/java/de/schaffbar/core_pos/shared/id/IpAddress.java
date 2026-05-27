package de.schaffbar.core_pos.shared.id;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.schaffbar.core_pos.shared.id.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class IpAddress {

    @JsonValue
    String value;

    @JsonCreator
    private IpAddress(String ipAddress) {
        ValueObjectAssert.notBlank(ipAddress, ValueObject.IP_ADDRESS);
        if (!isValidIpv4(ipAddress)) {
            throw new IllegalArgumentException("Invalid IP address format: " + ipAddress);
        }
        this.value = ipAddress;
    }

    public boolean sameValueAs(IpAddress other) {
        if (isNull(other)) {
            return false;
        }

        return this.value.equals(other.value);
    }

    // ------------------------------------------------------------------------
    // helper

    private static boolean isValidIpv4(String ip) {
        String[] parts = ip.split("\\.", -1);
        if (parts.length != 4) {
            return false;
        }

        for (String part : parts) {
            try {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
                if (part.length() > 1 && part.startsWith("0")) {
                    return false;
                }
            }
            catch (NumberFormatException e) {
                return false;
            }
        }

        return true;
    }

}
