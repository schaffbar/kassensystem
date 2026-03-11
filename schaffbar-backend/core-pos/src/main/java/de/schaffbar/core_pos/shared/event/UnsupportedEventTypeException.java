package de.schaffbar.core_pos.shared.event;

import java.io.Serial;

public class UnsupportedEventTypeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -335434623592172770L;

    public UnsupportedEventTypeException(String message) {
        super(message);
    }

}
