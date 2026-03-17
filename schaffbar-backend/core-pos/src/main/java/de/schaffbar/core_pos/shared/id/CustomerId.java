package de.schaffbar.core_pos.shared.id;

import static java.util.Objects.isNull;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.schaffbar.core_pos.shared.id.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class CustomerId {

    @JsonValue
    UUID value;

    @JsonCreator
    private CustomerId(UUID id) {
        ValueObjectAssert.notNull(id, ValueObject.CUSTOMER_ID);
        this.value = id;
    }

    public static CustomerId random() {
        return new CustomerId(UUID.randomUUID());
    }

    public boolean sameValueAs(CustomerId other) {
        if (isNull(other)) {
            return false;
        }

        return this.value.equals(other.value);
    }

}
