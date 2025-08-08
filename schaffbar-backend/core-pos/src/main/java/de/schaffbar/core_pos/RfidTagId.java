package de.schaffbar.core_pos;

import java.util.UUID;

import de.schaffbar.core_pos.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class RfidTagId {

    UUID value;

    private RfidTagId(UUID id) {
        ValueObjectAssert.notNull(id, ValueObject.RFID_TAG_ID);
        this.value = id;
    }

}
