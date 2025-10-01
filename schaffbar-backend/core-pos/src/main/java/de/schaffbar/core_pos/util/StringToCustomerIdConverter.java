package de.schaffbar.core_pos.util;

import java.util.UUID;

import de.schaffbar.core_pos.CustomerId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToCustomerIdConverter implements Converter<String, CustomerId> {

    @Override
    public CustomerId convert(String source) {
        return CustomerId.of(UUID.fromString(source));
    }

}
