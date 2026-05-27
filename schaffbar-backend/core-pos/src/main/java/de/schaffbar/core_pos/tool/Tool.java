package de.schaffbar.core_pos.tool;

import static java.util.Objects.isNull;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.exception.CustomerAlreadyInstructorException;
import de.schaffbar.core_pos.shared.exception.CustomerNotInstructorException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.IpAddress;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ToolId;
import de.schaffbar.core_pos.tool.ToolCommands.CreateToolCommand;
import de.schaffbar.core_pos.tool.ToolCommands.SetWlanRelaisCommand;
import de.schaffbar.core_pos.tool.ToolCommands.UpdateToolCommand;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "TOOL_INSTRUCTOR", schema = "SCHAFFBAR", joinColumns = @JoinColumn(name = "tool_id"))
    @Column(name = "instructor_id")
    private Set<UUID> instructorIds = new HashSet<>();

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

    public IpAddress getIpAddress() {
        if (isNull(this.ipAddress)) {
            return null;
        }

        return IpAddress.of(this.ipAddress);
    }

    public Set<CustomerId> getInstructors() {
        return this.instructorIds.stream() //
                .map(CustomerId::of) //
                .collect(Collectors.toSet());
    }

    public boolean isInstructor(CustomerId customerId) {
        return this.instructorIds.contains(customerId.getValue());
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

    public List<SchaffbarEvent> setWlanRelais(SetWlanRelaisCommand command) {
        setWlanRelaisType(command.wlanRelaisType());
        setIpAddress(command.ipAddress().getValue());
        setHttpStartCommand(command.wlanRelaisType().getHttpStartCommand());
        setOnCommand(command.wlanRelaisType().getOnCommand());
        setOffCommand(command.wlanRelaisType().getOffCommand());

        return List.of(ToolEventFactory.toolWlanRelaisSet(this));
    }

    public List<SchaffbarEvent> clearWlanRelais() {
        setWlanRelaisType(null);
        setIpAddress(null);
        setHttpStartCommand(null);
        setOnCommand(null);
        setOffCommand(null);

        return List.of(ToolEventFactory.toolWlanRelaisCleared(getId()));
    }

    public List<SchaffbarEvent> addInstructors(List<CustomerId> instructorIds) {
        for (CustomerId instructorId : instructorIds) {
            if (this.instructorIds.contains(instructorId.getValue())) {
                throw new CustomerAlreadyInstructorException(instructorId, getId());
            }
        }

        instructorIds.forEach(id -> this.instructorIds.add(id.getValue()));

        return List.of(ToolEventFactory.toolInstructorsAdded(getId(), instructorIds));
    }

    public List<SchaffbarEvent> removeInstructors(List<CustomerId> instructorIds) {
        for (CustomerId instructorId : instructorIds) {
            if (!this.instructorIds.contains(instructorId.getValue())) {
                throw new CustomerNotInstructorException(instructorId, getId());
            }
        }

        instructorIds.forEach(id -> this.instructorIds.remove(id.getValue()));

        return List.of(ToolEventFactory.toolInstructorsRemoved(getId(), instructorIds));
    }

}
