package de.schaffbar.core_pos.shared.event;

public enum EventType {

    // ------------------------------------------------------------------------
    // customer

    CUSTOMER_CREATED, //
    CUSTOMER_UPDATED, //
    CUSTOMER_CONTACT_CHANGED, //
    CUSTOMER_ADDRESS_CHANGED, //
    CUSTOMER_DELETED, //

    // ------------------------------------------------------------------------
    // tool

    TOOL_CREATED, //
    TOOL_UPDATED, //
    TOOL_WLAN_RELAIS_UPDATED, //
    TOOL_RFID_READER_ASSIGNED, //
    TOOL_RFID_READER_CLEARED, //
    TOOL_DELETED, //

    // ------------------------------------------------------------------------
    // tool usage

    TOOL_USAGE_STARTED, //
    TOOL_USAGE_STOPPED, //

    // ------------------------------------------------------------------------
    // RFID reader

    RFID_READER_CREATED, //
    RFID_READER_UPDATED, //
    RFID_READER_DELETED, //

    // ------------------------------------------------------------------------
    // RFID tag

    RFID_TAG_CREATED, //
    RFID_TAG_DELETED, //

    // ------------------------------------------------------------------------
    // RFID tag assignment

    RFID_TAG_ASSIGNMENT_REQUESTED, //
    RFID_TAG_ASSIGNED, //
    RFID_TAG_UNASSIGNED, //

    // ------------------------------------------------------------------------
    // workshop session

    WORKSHOP_SESSION_STARTED, //
    WORKSHOP_SESSION_CLOSED, //

    // ------------------------------------------------------------------------
    // workshop usage

    WORKSHOP_USAGE_ENTERED, // TODO: Consider renaming to WORKSHOP_ENTERED and WORKSHOP_EXITED
    WORKSHOP_USAGE_LEFT; //

    // ------------------------------------------------------------------------
    // static factory methods

    public static EventType toEnum(String eventTypeName) {
        for (EventType eventType : EventType.values()) {
            if (eventType.toString().equalsIgnoreCase(eventTypeName)) {
                return eventType;
            }
        }

        throw new UnsupportedEventTypeException("No matching enum value for name[" + eventTypeName + "]");
    }

}
