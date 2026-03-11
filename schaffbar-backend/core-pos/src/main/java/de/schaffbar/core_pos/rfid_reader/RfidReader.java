package de.schaffbar.core_pos.rfid_reader;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import de.schaffbar.core_pos.rfid_reader.RfidReaderCommands.UpdateRfidReaderCommand;
import de.schaffbar.core_pos.shared.event.SchaffbarEvent;
import de.schaffbar.core_pos.shared.id.MacAddress;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import jakarta.persistence.Column;
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
@Table(name = "RFID_READER", schema = "SCHAFFBAR")
class RfidReader {

    @Id
    private UUID id;

    @NotBlank
    @Column(unique = true)
    private String macAddress;

    @Enumerated(EnumType.STRING)
    private RfidReaderType type;

    private String name;

    private String socketName;

    @NotNull
    private Instant createdAt;

    @NotNull
    @Version
    private Instant updatedAt;

    // ------------------------------------------------------------------------
    // static constructor

    public static RfidReaderWithEvents of(MacAddress macAddress) {
        RfidReader rfidReader = new RfidReader();
        rfidReader.setId(RfidReaderId.random().getValue());
        rfidReader.setMacAddress(macAddress.getValue());
        rfidReader.setCreatedAt(Instant.now());

        List<SchaffbarEvent> events = List.of(RfidReaderEventFactory.rfidReaderCreated(rfidReader));

        return new RfidReaderWithEvents(rfidReader, events);
    }

    // ------------------------------------------------------------------------
    // query

    public RfidReaderId getId() {
        return RfidReaderId.of(this.id);
    }

    // ------------------------------------------------------------------------
    // command

    public List<SchaffbarEvent> update(UpdateRfidReaderCommand command) {
        setType(command.type());
        setName(command.name());
        setSocketName(command.socketName());

        return List.of(RfidReaderEventFactory.rfidReaderUpdated(this));
    }

    // ------------------------------------------------------------------------
    // helper

    record RfidReaderWithEvents(RfidReader rfidReader, List<SchaffbarEvent> events) {}

}
