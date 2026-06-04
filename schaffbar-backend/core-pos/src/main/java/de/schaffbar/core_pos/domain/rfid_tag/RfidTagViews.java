package de.schaffbar.core_pos.domain.rfid_tag;

import java.time.Instant;

import de.schaffbar.core_pos.shared.id.RfidTagId;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RfidTagViews {

    RfidTagViews MAPPER = Mappers.getMapper(RfidTagViews.class);

    // ------------------------------------------------------------------------
    // mapper

    RfidTagView toRfidTagView(RfidTag rfidTag);

    // ------------------------------------------------------------------------
    // views

    record RfidTagView( //
            @NotNull RfidTagId id, //
            boolean active, //
            @NotNull Instant createdAt //
    ) {}

}
