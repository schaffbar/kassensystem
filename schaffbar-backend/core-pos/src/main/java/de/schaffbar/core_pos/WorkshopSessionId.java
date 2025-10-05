package de.schaffbar.core_pos;

import java.util.UUID;

import de.schaffbar.core_pos.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class WorkshopSessionId {

    UUID value;

    private WorkshopSessionId(UUID id) {
        ValueObjectAssert.notNull(id, ValueObject.CUSTOMER_ID);
        this.value = id;
    }

}
