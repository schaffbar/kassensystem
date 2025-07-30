package de.schaffbar.core_pos;

import static java.util.Objects.isNull;

import java.util.UUID;

public interface ValueObjectMapper {

    default CustomerId toCustomerId(UUID id) {
        return isNull(id) ? null : CustomerId.of(id);
    }

    default RfidReaderId toRfidReader(UUID id) {
        return isNull(id) ? null : RfidReaderId.of(id);
    }

    default ToolId toToolId(UUID id) {
        return isNull(id) ? null : ToolId.of(id);
    }

}
