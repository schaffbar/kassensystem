package de.schaffbar.core_pos;

import static java.util.Objects.isNull;

import java.util.Optional;

import de.schaffbar.core_pos.rfid_reader.RfidReaderService;
import de.schaffbar.core_pos.rfid_reader.RfidReaderType;
import de.schaffbar.core_pos.rfid_reader.RfidReaderViews.RfidReaderView;
import de.schaffbar.core_pos.rfid_tag.RfidTagCommands.CreateRfidTagCommand;
import de.schaffbar.core_pos.rfid_tag.RfidTagService;
import de.schaffbar.core_pos.rfid_tag.RfidTagViews.RfidTagView;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentService;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentViews;
import de.schaffbar.core_pos.use_case.CustomerAssignRfidTag;
import de.schaffbar.core_pos.use_case.EnterWorkshop;
import de.schaffbar.core_pos.use_case.LeaveWorkshop;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/device")
public class TestController {

    private final @NonNull RfidTagService rfidTagService;

    private final @NonNull RfidReaderService rfidReaderService;

    private final @NonNull RfidTagAssignmentService rfidTagAssignmentService;

    private final @NonNull EnterWorkshop enterWorkshop;

    private final @NonNull LeaveWorkshop leaveWorkshop;

    private final @NonNull CustomerAssignRfidTag assignRfidTagUseCase;

    @PostMapping(value = "/init", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InitResponse> init(@RequestBody @NotNull @Valid InitRequestBody requestBody) {

        log.info("Init RFID reader. Received mac address: {}", requestBody.MACADDR());

        MacAddress macAddress = MacAddress.of(requestBody.MACADDR());

        RfidReaderId id = this.rfidReaderService.getRfidReader(macAddress) //
                .map(RfidReaderView::id) //
                .orElseGet(() -> this.rfidReaderService.createRfidReader(macAddress));

        RfidReaderView rfidReader = this.rfidReaderService.getRfidReader(id) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(id));

        String deviceNme = switch (rfidReader.type()) {
            case RFID_TAG_REGISTER -> "RFID Tag Register";
            case RFID_TAG_ASSIGNER -> "RFID Tag Assigner";
            case GATE_KEEPER -> "Gate Keeper";
            case GATE_KEEPER_IN -> "Gate Keeper In";
            case GATE_KEEPER_OUT -> "Gate Keeper Out";
            case SWITCH_BOX -> "Switch Box";
        };

        InitResponse response = InitResponse.builder() //
                .STATE(isNull(rfidReader.type()) ? "ERROR" : "START") //
                .DEVNAME(deviceNme) //
                .DEVUSECASE(isNull(rfidReader.type()) ? "ERROR" : rfidReader.type().getKey()) //
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

        log.info("Received mac address: {}", requestBody.MACADDR());
        log.info("Received RFID tag id: {}", requestBody.RFID());

        MacAddress macAddress = MacAddress.of(requestBody.MACADDR());
        RfidReaderView rfidReader = this.rfidReaderService.getRfidReader(macAddress) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(macAddress));

        RfidTagId rfidTagId = RfidTagId.of(requestBody.RFID());

        CounterResponse response = switch (rfidReader.type()) {
            case RfidReaderType.RFID_TAG_REGISTER -> registerRfidTag(rfidTagId);
            case RfidReaderType.RFID_TAG_ASSIGNER -> assignRfidTag(rfidTagId);
            default -> throw new IllegalStateException("Unexpected value: " + rfidReader.type());
        };

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/card", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceCardResponse> card(@RequestBody @NotNull @Valid DeviceCardRequestBody requestBody) {

        log.info("Received mac address: {}", requestBody.MACADDR());
        log.info("Received RFID tag id: {}", requestBody.RFID());

        MacAddress macAddress = MacAddress.of(requestBody.MACADDR());
        RfidReaderView rfidReader = this.rfidReaderService.getRfidReader(macAddress) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(macAddress));

        if (rfidReader.type() != RfidReaderType.GATE_KEEPER) {
            throw new RuntimeException("TODO: Invalid type of RFID reader");
        }

        RfidTagId rfidTagId = RfidTagId.of(requestBody.RFID());
        RfidTagView rfidTag = this.rfidTagService.getRfidTag(rfidTagId) //
                .orElseThrow(() -> ResourceNotFoundException.rfidTag(rfidTagId));

        CustomerId customerId = this.rfidTagAssignmentService.getRfidTagAssignment(rfidTag.id()) //
                .map(RfidTagAssignmentViews.RfidTagAssignmentView::customerId) //
                .orElseThrow(() -> new RuntimeException("TODO: No customer assigned to RFID tag"));

        this.enterWorkshop.process(customerId);

        this.leaveWorkshop.process(customerId);

        //        if (this.workshopUsageService.isCustomerInWorkshop(customerId)) {
        //            this.workshopUsageService.leaveWorkshop(customerId);
        //        }
        //        else {
        //            this.workshopUsageService.enterWorkshop(customerId);
        //        }

        DeviceCardResponse response = DeviceCardResponse.builder() //
                .DEVUSECASE(RfidReaderType.GATE_KEEPER.getKey()) //
                .ERROR("") //
                .STATE("END") //
                .ICON("HI") //
                .CUSTOMERNAME("Max Mustermann") //
                .CUSTOMERSTARTSTOP("3:00") //
                .UNITS("5:00") //
                .build();

        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------------------------
    // helper

    private CounterResponse registerRfidTag(RfidTagId rfid) {
        Optional<RfidTagView> rfidTag = this.rfidTagService.getRfidTag(rfid);
        if (rfidTag.isPresent()) {
            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_REGISTER.getKey()) //
                    .ERROR("RFID bereits vorhanden!") //
                    .STATE("END") //
                    .ICON("RFID") //
                    .build();
        }
        else {
            CreateRfidTagCommand command = new CreateRfidTagCommand(rfid.getValue());
            RfidTagId rfidTagId = this.rfidTagService.createRfidTag(command);

            log.info("Created RFID tag with id: {}", rfidTagId);

            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_REGISTER.getKey()) //
                    .ERROR("") //
                    .STATE("END") //
                    .ICON("OK") //
                    .build();
        }
    }

    private CounterResponse assignRfidTag(RfidTagId rfidTagId) {
        this.assignRfidTagUseCase.process(rfidTagId);

        log.info("Assigned RFID tag with id: {}", rfidTagId);

        return CounterResponse.builder() //
                .DEVUSECASE(RfidReaderType.RFID_TAG_ASSIGNER.getKey()) //
                .ERROR("") //
                .STATE("END") //
                .ICON("OK") //
                .build();
    }

    // ------------------------------------------------------------------------
    // Request and Response bodies

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

    @Builder
    public record DeviceCardRequestBody( //
            String MACADDR, //
            String RFID //
    ) {}

    @Builder
    public record DeviceCardResponse( //
            String DEVUSECASE,  //
            String ERROR,  //
            String STATE,  //
            String ICON,  //
            String CUSTOMERNAME,  //
            String CUSTOMERSTARTSTOP,  //
            String UNITS  //
    ) {}

}
