package de.schaffbar.core_pos;

import static java.util.Objects.isNull;

import java.util.Optional;

import de.schaffbar.core_pos.rfid_reader.RfidReaderCommands;
import de.schaffbar.core_pos.rfid_reader.RfidReaderService;
import de.schaffbar.core_pos.rfid_reader.RfidReaderViews;
import de.schaffbar.core_pos.rfid_reader.web.RfidReaderApiMapper;
import de.schaffbar.core_pos.rfid_reader.web.RfidReaderApiModel.RfidReaderApiDto;
import de.schaffbar.core_pos.rfid_tag.RfidTagCommands.CreateRfidTagCommand;
import de.schaffbar.core_pos.rfid_tag.RfidTagService;
import de.schaffbar.core_pos.rfid_tag.RfidTagViews.RfidTagView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/device")
public class TestController {

    private final @NonNull RfidTagService rfidTagService;

    private final @NonNull RfidReaderService rfidReaderService;

    @PostMapping(value = "/init", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InitResponse> init(@RequestBody @NotNull @Valid InitRequestBody requestBody) {

        System.out.println("Received mac address: " + requestBody.MACADDR());

        MacAddress macAddress = MacAddress.of(requestBody.MACADDR());

        RfidReaderId id = this.rfidReaderService.getRfidReader(macAddress) //
                .map(RfidReaderViews.RfidReaderView::id) //
                .orElseGet(() -> this.rfidReaderService.createRfidReader(new RfidReaderCommands.CreateRfidReaderCommand(macAddress)));

        RfidReaderApiDto rfidReader = this.rfidReaderService.getRfidReader(id) //
                .map(RfidReaderApiMapper.MAPPER::toRfidReaderApiDto) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(id));

        InitResponse response = InitResponse.builder() //
                .STATE(isNull(rfidReader.type()) ? "ERROR" : "START") //
                .DEVNAME("Schaffbar Counter") //
                .DEVUSECASE(isNull(rfidReader.type()) ? "ERROR" : rfidReader.type()) //
                .TERMINAL("") //
                .ERROR(isNull(rfidReader.type()) ? "Nicht Gefunden" : "") //
                .DATEY(2025) //
                .DATEM(8) //
                .DATED(8) //
                .TIMEH(2) //
                .TIMEM(2) //
                .TIMES(2) //
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/counter", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CounterResponse> counter(@RequestBody @NotNull @Valid RfidTagRequestBody requestBody) {

        System.out.println("Received mac address: " + requestBody.MACADDR());
        System.out.println("Received rfid id: " + requestBody.RFID());

        RfidTagId id = RfidTagId.of(requestBody.RFID());

        Optional<RfidTagView> rfidTag = this.rfidTagService.getRfidTag(id);
        if (rfidTag.isPresent()) {
            CounterResponse response = CounterResponse.builder() //
                    .DEVUSECASE("A") //
                    .ERROR("RFID bereits vorhanden!") //
                    .STATE("END") //
                    .ICON("RFID") //
                    .build();

            return ResponseEntity.ok(response);
        }
        else {
            CreateRfidTagCommand command = new CreateRfidTagCommand(requestBody.RFID());
            RfidTagId rfidTagId = this.rfidTagService.createRfidTag(command);

            CounterResponse response = CounterResponse.builder() //
                    .DEVUSECASE("A") //
                    .ERROR("") //
                    .STATE("END") //
                    .ICON("OK") //
                    .build();

            return ResponseEntity.ok(response);
        }
    }

    @Builder
    public record InitRequestBody( //
            String MACADDR //
    ) {}

    @Builder
    public record InitResponse( //
            String STATE,  //
            String DEVNAME,  //
            String STARTHTTP,  //
            String DEVIP,  //
            String SWITCHON,  //
            String SWITCHOFF,  //
            String DEVUSECASE,  //
            String TERMINAL,  //
            String ERROR, //
            int DATEY, //
            int DATEM, //
            int DATED, //
            int TIMEH, //
            int TIMEM, //
            int TIMES //
    ) {}

    @Builder
    public record RfidTagRequestBody( //
            String MACADDR, //
            String RFID //
    ) {}

    @Builder
    public record CounterResponse( //
            String DEVUSECASE,  //
            String ERROR,  //
            String STATE,  //
            String ICON  //
    ) {}

}
