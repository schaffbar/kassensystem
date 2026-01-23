package de.schaffbar.core_pos.shared.id;

import java.util.UUID;

import de.schaffbar.core_pos.shared.id.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class RfidReaderId {

    UUID value;

    private RfidReaderId(UUID id) {
        ValueObjectAssert.notNull(id, ValueObject.RFID_READER_ID);
        this.value = id;
    }

}
