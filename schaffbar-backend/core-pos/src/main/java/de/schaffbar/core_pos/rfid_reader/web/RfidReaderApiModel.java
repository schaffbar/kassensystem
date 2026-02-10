package de.schaffbar.core_pos.rfid_reader.web;

import java.time.Instant;

import de.schaffbar.core_pos.rfid_reader.RfidReaderCommands.UpdateRfidReaderCommand;
import de.schaffbar.core_pos.rfid_reader.RfidReaderViews.RfidReaderView;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.ValueObjectMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RfidReaderApiModel extends ValueObjectMapper {

    RfidReaderApiModel MAPPER = Mappers.getMapper(RfidReaderApiModel.class);

    // ------------------------------------------------------------------------
    // mapping view to response

    @Mapping(target = "id", source = "id.value")
    RfidReaderApiDto toRfidReaderApiDto(RfidReaderView rfidReader);

    // ------------------------------------------------------------------------
    // mapping request body to command

    UpdateRfidReaderCommand toRfidReaderCommand(RfidReaderId id, UpdateRfidReaderRequestBody requestBody);

    // ------------------------------------------------------------------------
    // response

    record RfidReaderApiDto( //
            @NotBlank String id, //
            @NotBlank String macAddress, //
            String type, //
            String name, //
            String socketName, //
            @NotNull Instant createdAt //
    ) {}

    // ------------------------------------------------------------------------
    // request body

    record CreateRfidReaderRequestBody( //
            @NotBlank String macAddress  //
    ) {}

    record UpdateRfidReaderRequestBody( //
            @NotBlank String type, //
            String name, //
            String socketName //
    ) {}

}
