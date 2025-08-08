package de.schaffbar.core_pos.rfid_tag;

import java.time.Instant;

import de.schaffbar.core_pos.RfidTagId;
import de.schaffbar.core_pos.rfid_tag.RfidTagCommands.CreateRfidTagCommand;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
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
@Table(name = "RFID_TAG", schema = "SCHAFFBAR")
class RfidTag {

    @Id
    private String id;

    private boolean active;

    @NotNull
    private Instant createdAt;

    @NotNull
    @Version
    private Instant updatedAt;

    // ------------------------------------------------------------------------
    // static constructor

    public static RfidTag of(CreateRfidTagCommand command) {
        RfidTag rfidTag = new RfidTag();
        rfidTag.setId(command.rfidTagId());
        rfidTag.setActive(true);
        rfidTag.setCreatedAt(Instant.now());

        return rfidTag;
    }

    // ------------------------------------------------------------------------
    // query

    public RfidTagId getId() {
        return RfidTagId.of(this.id);
    }

    // ------------------------------------------------------------------------
    // command

}
