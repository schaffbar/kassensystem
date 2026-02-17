package de.schaffbar.core_pos.tool;

import static java.util.Objects.isNull;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateToolCommand;
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

    // TODO: add WLAN-Relais configuration

    @NotNull
    private Instant createdAt;

    @NotNull
    @Version
    private Instant updatedAt;

    // ------------------------------------------------------------------------
    // static constructor

    public static Tool of(CreateToolCommand command) {
        Tool tool = new Tool();
        tool.setId(UUID.randomUUID());
        tool.setName(command.name());
        tool.setDescription(command.description());
        tool.setCreatedAt(Instant.now());

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

}
