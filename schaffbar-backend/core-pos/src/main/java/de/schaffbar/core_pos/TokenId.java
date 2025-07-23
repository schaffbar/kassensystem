package de.schaffbar.core_pos;

import de.schaffbar.core_pos.ValueObjectAssert.ValueObject;
import lombok.Value;

@Value(staticConstructor = "of")
public class TokenId {

    String value;

    private TokenId(String id) {
        ValueObjectAssert.notBlank(id, ValueObject.TOKEN_ID);
        this.value = id;
    }

}
