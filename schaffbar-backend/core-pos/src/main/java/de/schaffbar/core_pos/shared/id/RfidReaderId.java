package de.schaffbar.core_pos.shared.id;

import static java.util.Objects.isNull;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.schaffbar.core_pos.shared.id.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class RfidReaderId {

    @JsonValue
    UUID value;

    @JsonCreator
    private RfidReaderId(UUID id) {
        ValueObjectAssert.notNull(id, ValueObject.RFID_READER_ID);
        this.value = id;
    }

    public static RfidReaderId random() {
        return new RfidReaderId(UUID.randomUUID());
    }

    public boolean sameValueAs(RfidReaderId other) {
        if (isNull(other)) {
            return false;
        }

        return this.value.equals(other.value);
    }

}
