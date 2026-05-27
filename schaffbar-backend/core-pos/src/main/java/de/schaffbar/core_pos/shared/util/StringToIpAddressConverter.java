package de.schaffbar.core_pos.shared.util;

import de.schaffbar.core_pos.shared.id.IpAddress;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToIpAddressConverter implements Converter<String, IpAddress> {

    @Override
    public IpAddress convert(String source) {
        return IpAddress.of(source);
    }

}
