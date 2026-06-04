package de.schaffbar.core_pos.domain.rfid_reader;

import java.time.Instant;

import de.schaffbar.core_pos.shared.id.RfidReaderId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RfidReaderViews {

    RfidReaderViews MAPPER = Mappers.getMapper(RfidReaderViews.class);

    // ------------------------------------------------------------------------
    // mapper

    RfidReaderView toRfidReaderView(RfidReader rfidReader);

    // ------------------------------------------------------------------------
    // views

    record RfidReaderView( //
            @NotNull RfidReaderId id, //
            @NotBlank String macAddress, //
            RfidReaderType type, //
            String name, //
            String socketName, //
            @NotNull Instant createdAt //
    ) {}

}
