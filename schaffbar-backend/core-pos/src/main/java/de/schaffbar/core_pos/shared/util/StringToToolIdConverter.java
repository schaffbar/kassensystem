package de.schaffbar.core_pos.shared.util;

import java.util.UUID;

import de.schaffbar.core_pos.shared.id.ToolId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToToolIdConverter implements Converter<String, ToolId> {

    @Override
    public ToolId convert(String source) {
        return ToolId.of(UUID.fromString(source));
    }

}
