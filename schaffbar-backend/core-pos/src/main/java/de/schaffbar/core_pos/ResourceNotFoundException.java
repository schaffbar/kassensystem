package de.schaffbar.core_pos;

import java.io.Serial;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ResourceNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final String MESSAGE = "Resource %s [id: %s] does not exist!";

    private ResourceNotFoundException(Resource resource, String id) {
        super(buildExceptionMessage(resource, id), null);
    }

    public static ResourceNotFoundException customer(CustomerId id) {
        return new ResourceNotFoundException(Resource.CUSTOMER, id.getValue().toString());
    }

    public static ResourceNotFoundException tool(ToolId id) {
        return new ResourceNotFoundException(Resource.TOOL, id.getValue().toString());
    }

    public static ResourceNotFoundException rfidReader(RfidReaderId id) {
        return new ResourceNotFoundException(Resource.RFID_READER, id.getValue().toString());
    }

    public static ResourceNotFoundException rfidTag(RfidTagId id) {
        return new ResourceNotFoundException(Resource.RFID_TAG, id.getValue().toString());
    }

    // ------------------------------------------------------------------------
    // helper

    private static String buildExceptionMessage(Resource resource, String id) {
        return String.format(MESSAGE, resource.getValue(), id);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Resource {

        CUSTOMER("customer"), //
        TOOL("tool"), //
        RFID_READER("RFID reader"), //
        RFID_TAG("RFID tag"), //
        TOKEN("token");

        private final String value;

    }

}
