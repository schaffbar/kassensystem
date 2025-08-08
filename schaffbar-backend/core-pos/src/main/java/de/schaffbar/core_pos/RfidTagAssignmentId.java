package de.schaffbar.core_pos;

import java.util.UUID;

import de.schaffbar.core_pos.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class RfidTagAssignmentId {

    UUID value;

    private RfidTagAssignmentId(UUID id) {
        ValueObjectAssert.notNull(id, ValueObject.RFID_TAG_ASSIGNMENT_ID);
        this.value = id;
    }

}
