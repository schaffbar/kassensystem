package de.schaffbar.core_pos.shared.id;

import static java.util.Objects.isNull;

import java.util.UUID;

import de.schaffbar.core_pos.shared.id.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class ToolId {

    UUID value;

    private ToolId(UUID id) {
        ValueObjectAssert.notNull(id, ValueObject.TOOL_ID);
        this.value = id;
    }

    public static ToolId random() {
        return ToolId.of(UUID.randomUUID());
    }

    public boolean sameValueAs(ToolId other) {
        if (isNull(other)) {
            return false;
        }

        return this.value.equals(other.value);
    }

}
