package de.schaffbar.core_pos.shared.util;

import de.schaffbar.core_pos.shared.id.MacAddress;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToMacAddressConverter implements Converter<String, MacAddress> {

    @Override
    public MacAddress convert(String source) {
        return MacAddress.of(source);
    }

}
