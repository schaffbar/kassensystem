package de.schaffbar.core_pos;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ValueObjectAssert {

    static final String NULL_ERROR_MESSAGE = "The value of %s must not be null";

    static final String BLANK_ERROR_MESSAGE = "The value of %s must not be blank";

    static final String POSITIVE_ERROR_MESSAGE = "The value of %s must be positive";

    public static void notNull(Object value, ValueObject valueObject) {
        if (isNull(value)) {
            throw new IllegalArgumentException(notNullMessage(valueObject));
        }
    }

    public static void notBlank(String value, ValueObject valueObject) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(notBlankMessage(valueObject));
        }
    }

    public static void positive(long value, ValueObject valueObject) {
        if (value <= 0) {
            throw new IllegalArgumentException(positiveMessage(valueObject));
        }
    }

    public static void positive(int value, ValueObject valueObject) {
        if (value <= 0) {
            throw new IllegalArgumentException(positiveMessage(valueObject));
        }
    }

    // ------------------------------------------------------------------------
    // helper

    static String notNullMessage(ValueObject valueObject) {
        return String.format(NULL_ERROR_MESSAGE, valueObject.getValue());
    }

    static String notBlankMessage(ValueObject valueObject) {
        return String.format(BLANK_ERROR_MESSAGE, valueObject.getValue());
    }

    static String positiveMessage(ValueObject valueObject) {
        return String.format(POSITIVE_ERROR_MESSAGE, valueObject.getValue());
    }

    // ------------------------------------------------------------------------
    // enum

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum ValueObject {

        CUSTOMER_ID("customer id"), //
        TOOL_ID("tool id"), //
        RFID_READER_ID("rfid reader id"), //
        TOKEN_ASSIGNMENT_ID("token assignment id"), //
        TOKEN_ID("token id");

        private final String value;

    }

}
