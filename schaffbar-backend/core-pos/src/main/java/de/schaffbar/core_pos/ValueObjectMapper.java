package de.schaffbar.core_pos;

import static java.util.Objects.isNull;

import java.util.UUID;

public interface ValueObjectMapper {

    default CustomerId toCustomerId(UUID id) {
        return isNull(id) ? null : CustomerId.of(id);
    }

    default RfidReaderId toRfidReaderId(UUID id) {
        return isNull(id) ? null : RfidReaderId.of(id);
    }

    default MacAddress toMacAddress(String macAddress) {
        return isNull(macAddress) ? null : MacAddress.of(macAddress);
    }

    default RfidTagId toRfidTagId(UUID id) {
        return isNull(id) ? null : RfidTagId.of(id);
    }

    default ToolId toToolId(UUID id) {
        return isNull(id) ? null : ToolId.of(id);
    }

}
