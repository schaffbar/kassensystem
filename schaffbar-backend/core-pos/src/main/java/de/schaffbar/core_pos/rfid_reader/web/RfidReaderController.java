package de.schaffbar.core_pos.rfid_reader.web;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.id.MacAddress;
import de.schaffbar.core_pos.id.RfidReaderId;
import de.schaffbar.core_pos.rfid_reader.RfidReaderService;
import de.schaffbar.core_pos.rfid_reader.RfidReaderViews.RfidReaderView;
import de.schaffbar.core_pos.rfid_reader.web.RfidReaderApiModel.CreateRfidReaderRequestBody;
import de.schaffbar.core_pos.rfid_reader.web.RfidReaderApiModel.RfidReaderApiDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/rfid-readers")
public class RfidReaderController {

    private final @NonNull RfidReaderService rfidReaderService;

    // ------------------------------------------------------------------------
    // query

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RfidReaderApiDto>> getAllRfidReaders() {
        List<RfidReaderApiDto> rfidReaders = this.rfidReaderService.getRfidReaders().stream() //
                .map(RfidReaderApiModel.MAPPER::toRfidReaderApiDto) //
                .toList();

        return ResponseEntity.ok(rfidReaders);
    }

    // TODO: REST - remove configuration from path, use just /api/v1/rfid-readers?macAddress=...
    @GetMapping(value = "/configuration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RfidReaderApiDto> getRfidReaderConfiguration(@RequestParam(name = "macAddress") @NotBlank String address) {
        MacAddress macAddress = MacAddress.of(address);

        RfidReaderId id = this.rfidReaderService.getRfidReader(macAddress) //
                .map(RfidReaderView::id) //
                .orElseGet(() -> this.rfidReaderService.createRfidReader(macAddress));

        RfidReaderApiDto rfidReader = this.rfidReaderService.getRfidReader(id) //
                .map(RfidReaderApiModel.MAPPER::toRfidReaderApiDto) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(id));

        return ResponseEntity.ok(rfidReader);
    }

    @GetMapping(value = "/{rfidReaderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RfidReaderApiDto> getRfidReader(@PathVariable @NotNull UUID rfidReaderId) {
        RfidReaderId id = RfidReaderId.of(rfidReaderId);
        RfidReaderApiDto rfidReader = this.rfidReaderService.getRfidReader(id) //
                .map(RfidReaderApiModel.MAPPER::toRfidReaderApiDto) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(id));

        return ResponseEntity.ok(rfidReader);
    }

    // ------------------------------------------------------------------------
    // command

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createRfidReader(@RequestBody @NotNull @Valid CreateRfidReaderRequestBody requestBody) {
        MacAddress macAddress = MacAddress.of(requestBody.macAddress());
        RfidReaderId rfidReaderId = this.rfidReaderService.createRfidReader(macAddress);
        URI location = URI.create("/api/v1/rfid-readers/" + rfidReaderId.getValue());

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/{rfidReaderId}")
    public ResponseEntity<Void> deleteRfidReader(@PathVariable @NotNull UUID rfidReaderId) {
        RfidReaderId id = RfidReaderId.of(rfidReaderId);
        this.rfidReaderService.deleteRfidReader(id);

        return ResponseEntity.noContent().build();
    }

}
