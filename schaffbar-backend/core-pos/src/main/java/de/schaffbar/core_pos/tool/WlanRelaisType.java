package de.schaffbar.core_pos.tool;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum WlanRelaisType {

    SHELLY("http", "/relay/0?turn=on", "/relay/0?turn=off");

    private final String httpStartCommand;

    private final String onCommand;

    private final String offCommand;

}
