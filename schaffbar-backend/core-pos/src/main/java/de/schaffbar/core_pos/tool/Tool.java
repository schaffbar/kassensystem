package de.schaffbar.core_pos.tool;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.ToolId;
import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
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
        // TODO: implement it
        tool.setCreatedAt(Instant.now());

        return tool;
    }

    // ------------------------------------------------------------------------
    // query

    public ToolId getId() {
        return ToolId.of(this.id);
    }

    // ------------------------------------------------------------------------
    // command

}
