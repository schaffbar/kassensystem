package de.schaffbar.core_pos.tool;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateWlanRelaisCommand;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    private WlanRelaisType wlanRelaisType;

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

    public static ToolWithEvents of(CreateToolCommand command) {
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

        List<SchaffbarEvent> events = List.of(ToolEventFactory.toolCreated(tool));

        return new ToolWithEvents(tool, events);
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

    public List<SchaffbarEvent> update(UpdateToolCommand command) {
        setName(command.name());
        setDescription(command.description());

        return List.of(ToolEventFactory.toolUpdated(this));
    }

    public List<SchaffbarEvent> assignRfidReader(RfidReaderId rfidReaderId) {
        setRfidReaderId(rfidReaderId.getValue());

        return List.of(ToolEventFactory.toolRfidReaderAssigned(getId(), rfidReaderId));
    }

    public List<SchaffbarEvent> clearRfidReader() {
        setRfidReaderId(null);

        return List.of(ToolEventFactory.toolRfidReaderCleared(getId()));
    }

    public List<SchaffbarEvent> updateWlanRelais(UpdateWlanRelaisCommand command) {
        if (isNull(command.wlanRelaisType())) {
            clearWlanRelais();
        }
        else {
            applyWlanRelaisType(command.wlanRelaisType(), command.ipAddress());
        }

        return List.of(ToolEventFactory.toolWlanRelaisUpdated(this));
    }

    public void clearWlanRelais() {
        setWlanRelaisType(null);
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

        setWlanRelaisType(type);
        setIpAddress(ipAddress);
        setHttpStartCommand(type.getHttpStartCommand());
        setOnCommand(type.getOnCommand());
        setOffCommand(type.getOffCommand());
    }

    record ToolWithEvents(Tool tool, List<SchaffbarEvent> events) {}

}
