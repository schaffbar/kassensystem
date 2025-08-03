package de.schaffbar.core_pos.rfid_tag.web;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.RfidTagId;
import de.schaffbar.core_pos.rfid_tag.RfidTagCommands.CreateRfidTagCommand;
import de.schaffbar.core_pos.rfid_tag.RfidTagService;
import de.schaffbar.core_pos.rfid_tag.web.RfidTagApiModel.CreateRfidTagRequestBody;
import de.schaffbar.core_pos.rfid_tag.web.RfidTagApiModel.RfidTagApiDto;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/rfid-tags")
public class RfidTagController {

    private final @NonNull RfidTagService rfidTagService;

    // ------------------------------------------------------------------------
    // query

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RfidTagApiDto>> getAllRfidTags() {
        List<RfidTagApiDto> rfidTags = this.rfidTagService.getRfidTags().stream() //
                .map(RfidTagApiMapper.MAPPER::toRfidTagApiDto) //
                .toList();

        return ResponseEntity.ok(rfidTags);
    }

    @GetMapping(value = "/{rfidTagId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RfidTagApiDto> getRfidTag(@PathVariable @NotNull UUID rfidTagId) {
        RfidTagId id = RfidTagId.of(rfidTagId);
        RfidTagApiDto rfidTag = this.rfidTagService.getRfidTag(id) //
                .map(RfidTagApiMapper.MAPPER::toRfidTagApiDto) //
                .orElseThrow(() -> ResourceNotFoundException.rfidTag(id));

        return ResponseEntity.ok(rfidTag);
    }

    // ------------------------------------------------------------------------
    // command

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createRfidTag(@RequestBody @NotNull @Valid CreateRfidTagRequestBody requestBody) {
        CreateRfidTagCommand command = RfidTagApiMapper.MAPPER.toCreateRfidTagCommand(requestBody);
        RfidTagId rfidTagId = this.rfidTagService.createRfidTag(command);
        URI location = URI.create("/api/v1/rfid-tags/" + rfidTagId.getValue());

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/{rfidTagId}")
    public ResponseEntity<Void> deleteRfidTag(@PathVariable @NotNull UUID rfidTagId) {
        RfidTagId id = RfidTagId.of(rfidTagId);
        this.rfidTagService.deleteRfidTag(id);

        return ResponseEntity.noContent().build();
    }

}
