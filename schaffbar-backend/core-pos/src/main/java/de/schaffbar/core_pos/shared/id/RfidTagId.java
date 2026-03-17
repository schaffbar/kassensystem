package de.schaffbar.core_pos.shared.id;

import static java.util.Objects.isNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.schaffbar.core_pos.shared.id.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class RfidTagId {

    @JsonValue
    String value;

    @JsonCreator
    private RfidTagId(String id) {
        ValueObjectAssert.notBlank(id, ValueObject.RFID_TAG_ID);
        this.value = id;
    }

    public boolean sameValueAs(RfidTagId other) {
        if (isNull(other)) {
            return false;
        }

        return this.value.equals(other.value);
    }

}
