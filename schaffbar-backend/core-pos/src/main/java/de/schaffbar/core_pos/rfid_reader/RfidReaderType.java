package de.schaffbar.core_pos.rfid_reader;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RfidReaderType {
    RFID_TAG_REGISTER("A"), //
    RFID_TAG_ASSIGNER("C"), //
    GATE_KEEPER_IN("GI"), //
    GATE_KEEPER_OUT("GO"), //
    SWITCH_BOX("S");

    @Getter
    private final String key;
}
