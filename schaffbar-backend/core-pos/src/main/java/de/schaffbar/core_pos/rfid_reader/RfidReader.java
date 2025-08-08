package de.schaffbar.core_pos.rfid_reader;

import java.time.Instant;
import java.util.UUID;

import de.schaffbar.core_pos.RfidReaderId;
import de.schaffbar.core_pos.rfid_reader.RfidReaderCommands.CreateRfidReaderCommand;
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

    // TODO: check unique constraint for mac address
    @NotBlank
    private String macAddress;

    @Enumerated(EnumType.STRING)
    private RfidReaderType type;

    @NotNull
    private Instant createdAt;

    @NotNull
    @Version
    private Instant updatedAt;

    // ------------------------------------------------------------------------
    // static constructor

    public static RfidReader of(CreateRfidReaderCommand command) {
        RfidReader rfidReader = new RfidReader();
        rfidReader.setId(UUID.randomUUID());
        rfidReader.setMacAddress(command.macAddress().getValue());
        // TODO: implement it
        rfidReader.setCreatedAt(Instant.now());

        return rfidReader;
    }

    // ------------------------------------------------------------------------
    // query

    public RfidReaderId getId() {
        return RfidReaderId.of(this.id);
    }

    // ------------------------------------------------------------------------
    // command

}
