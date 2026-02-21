package de.schaffbar.core_pos.shared.id;

import static java.util.Objects.isNull;

import java.util.UUID;

import de.schaffbar.core_pos.shared.id.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class WorkshopUsageId {

    UUID value;

    private WorkshopUsageId(UUID id) {
        ValueObjectAssert.notNull(id, ValueObject.CUSTOMER_ID);
        this.value = id;
    }

    public static WorkshopUsageId random() {
        return WorkshopUsageId.of(UUID.randomUUID());
    }

    public boolean sameValueAs(WorkshopUsageId other) {
        if (isNull(other)) {
            return false;
        }

        return this.value.equals(other.value);
    }

}
