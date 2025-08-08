package de.schaffbar.core_pos;

import de.schaffbar.core_pos.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class RfidTagId {

    String value;

    private RfidTagId(String id) {
        ValueObjectAssert.notBlank(id, ValueObject.RFID_TAG_ID);
        this.value = id;
    }

}
