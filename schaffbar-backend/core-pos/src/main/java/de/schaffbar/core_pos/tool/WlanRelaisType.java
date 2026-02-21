package de.schaffbar.core_pos.tool;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum WlanRelaisType {

    SHELLY_1("http://{{ipAddress}}/relay/0?", "turn=on", "turn=off"), //
    SHELLY_2("http://{{ipAddress}}/relay/0?'", "turn=on", "turn=off"), //
    SHELLY_PRO("http://{{ipAddress}}/rpc/Switch.Set?", "id=0&on=true", "id=0&on=false");

    private final String httpStartCommand;

    private final String onCommand;

    private final String offCommand;

}
