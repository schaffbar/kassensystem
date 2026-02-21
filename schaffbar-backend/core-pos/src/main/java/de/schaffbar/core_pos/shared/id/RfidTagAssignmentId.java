package de.schaffbar.core_pos.shared.id;

import static java.util.Objects.isNull;

import java.util.UUID;

import de.schaffbar.core_pos.shared.id.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class RfidTagAssignmentId {

    UUID value;

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
