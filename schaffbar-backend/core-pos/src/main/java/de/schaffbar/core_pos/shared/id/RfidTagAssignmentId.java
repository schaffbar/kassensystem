package de.schaffbar.core_pos.shared.id;

import static java.util.Objects.isNull;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.schaffbar.core_pos.shared.id.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class RfidTagAssignmentId {

    @JsonValue
    UUID value;

    @JsonCreator
    private RfidTagAssignmentId(UUID id) {
        ValueObjectAssert.notNull(id, ValueObject.RFID_TAG_ASSIGNMENT_ID);
        this.value = id;
    }

    public static RfidTagAssignmentId random() {
        return RfidTagAssignmentId.of(UUID.randomUUID());
    }

    public boolean sameValueAs(RfidTagAssignmentId other) {
        if (isNull(other)) {
            return false;
        }

        return this.value.equals(other.value);
    }

}
