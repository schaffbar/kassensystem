package de.schaffbar.core_pos.tool;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateWlanRelaisCommand;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "TOOL", schema = "SCHAFFBAR")
class Tool {

    @Id
    private UUID id;

    @NotBlank
    private String name;

    private String description;

    private UUID rfidReaderId;

    private String ipAddress;

    private String httpStartCommand;

    private String onCommand;

    private String offCommand;

    @NotNull
    private Instant createdAt;

    @NotNull
    @Version
    private Instant updatedAt;

    // ------------------------------------------------------------------------
    // static constructor

    public static Tool of(CreateToolCommand command) {
        Tool tool = new Tool();
        tool.setId(ToolId.random().getValue());
        tool.setName(command.name());
        tool.setDescription(command.description());
        tool.setCreatedAt(Instant.now());

        if (nonNull(command.rfidReaderId())) {
            tool.setRfidReaderId(command.rfidReaderId().getValue());
        }

        if (nonNull(command.wlanRelaisType())) {
            tool.applyWlanRelaisType(command.wlanRelaisType(), command.ipAddress());
        }

        return tool;
    }

    // ------------------------------------------------------------------------
    // query

    public ToolId getId() {
        return ToolId.of(this.id);
    }

    public RfidReaderId getRfidReaderId() {
        if (isNull(this.rfidReaderId)) {
            return null;
        }

        return RfidReaderId.of(this.rfidReaderId);
    }

    // ------------------------------------------------------------------------
    // command

    public void update(UpdateToolCommand command) {
        setName(command.name());
        setDescription(command.description());
    }

    public void assignRfidReader(RfidReaderId rfidReaderId) {
        setRfidReaderId(rfidReaderId.getValue());
    }

    public void clearRfidReader() {
        setRfidReaderId(null);
    }

    public void updateWlanRelais(UpdateWlanRelaisCommand command) {
        if (isNull(command.wlanRelaisType())) {
            clearWlanRelais();
            return;
        }

        applyWlanRelaisType(command.wlanRelaisType(), command.ipAddress());
    }

    public void clearWlanRelais() {
        setIpAddress(null);
        setHttpStartCommand(null);
        setOnCommand(null);
        setOffCommand(null);
    }

    // ------------------------------------------------------------------------
    // helper

    private void applyWlanRelaisType(WlanRelaisType type, String ipAddress) {
        if (isNull(ipAddress)) {
            throw new IllegalArgumentException("IP address is required when WLAN-Relais type is set");
        }

        setIpAddress(ipAddress);
        setHttpStartCommand(type.getHttpStartCommand());
        setOnCommand(type.getOnCommand());
        setOffCommand(type.getOffCommand());
    }

}
