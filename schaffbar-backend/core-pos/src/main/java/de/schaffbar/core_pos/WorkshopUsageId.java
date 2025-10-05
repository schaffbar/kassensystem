package de.schaffbar.core_pos;

import java.util.UUID;

import de.schaffbar.core_pos.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class WorkshopUsageId {

    UUID value;

    private WorkshopUsageId(UUID id) {
        ValueObjectAssert.notNull(id, ValueObject.CUSTOMER_ID);
        this.value = id;
    }

}
