package de.schaffbar.core_pos;

import java.util.UUID;

import de.schaffbar.core_pos.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class TokenAssignmentId {

    UUID value;

    private TokenAssignmentId(UUID id) {
        ValueObjectAssert.notNull(id, ValueObject.TOKEN_ASSIGNMENT_ID);
        this.value = id;
    }

}
