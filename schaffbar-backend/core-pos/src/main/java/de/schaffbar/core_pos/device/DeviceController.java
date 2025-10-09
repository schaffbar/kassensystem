package de.schaffbar.core_pos.device;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.Optional;

import de.schaffbar.core_pos.ResourceNotFoundException;
import de.schaffbar.core_pos.customer.CustomerService;
import de.schaffbar.core_pos.customer.CustomerViews.CustomerView;
import de.schaffbar.core_pos.device.DeviceApiModel.CounterResponse;
import de.schaffbar.core_pos.device.DeviceApiModel.DeviceCardRequestBody;
import de.schaffbar.core_pos.device.DeviceApiModel.DeviceCardResponse;
import de.schaffbar.core_pos.device.DeviceApiModel.InitRequestBody;
import de.schaffbar.core_pos.device.DeviceApiModel.InitResponse;
import de.schaffbar.core_pos.device.DeviceApiModel.RfidTagRequestBody;
import de.schaffbar.core_pos.id.CustomerId;
import de.schaffbar.core_pos.id.MacAddress;
import de.schaffbar.core_pos.id.RfidReaderId;
import de.schaffbar.core_pos.id.RfidTagId;
import de.schaffbar.core_pos.rfid_reader.RfidReaderService;
import de.schaffbar.core_pos.rfid_reader.RfidReaderType;
import de.schaffbar.core_pos.rfid_reader.RfidReaderViews.RfidReaderView;
import de.schaffbar.core_pos.rfid_tag.RfidTagCommands.CreateRfidTagCommand;
import de.schaffbar.core_pos.rfid_tag.RfidTagService;
import de.schaffbar.core_pos.rfid_tag.RfidTagViews.RfidTagView;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentService;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentViews.RfidTagAssignmentView;
import de.schaffbar.core_pos.use_case.CustomerAssignRfidTag;
import de.schaffbar.core_pos.use_case.EnterWorkshop;
import de.schaffbar.core_pos.use_case.LeaveWorkshop;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
public class DeviceController {

    private final @NonNull CustomerService customerService;

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

        String deviceNme;
        if (isNull(rfidReader.type())) {
            deviceNme = "ERROR";
        }
        else {
            deviceNme = switch (rfidReader.type()) {
                case RFID_TAG_REGISTER -> "RFID Tag Register";
                case RFID_TAG_ASSIGNER -> "RFID Tag Assigner";
                case GATE_KEEPER -> "Gate Keeper";
                case GATE_KEEPER_IN -> "Gate Keeper In";
                case GATE_KEEPER_OUT -> "Gate Keeper Out";
                case SWITCH_BOX -> "Switch Box";
            };
        }

        LocalDateTime now = LocalDateTime.now();
        InitResponse response = InitResponse.builder() //
                .STATE(isNull(rfidReader.type()) ? "ERROR" : "START") //
                .DEVNAME(deviceNme) //
                .DEVUSECASE(isNull(rfidReader.type()) ? "ERROR" : rfidReader.type().getKey()) //
                .TERMINAL("") //
                .ERROR(isNull(rfidReader.type()) ? "Nicht Gefunden" : "") //
                .DATEY(now.getYear()) //
                .DATEM(now.getMonthValue()) //
                .DATED(now.getDayOfMonth()) //
                .TIMEH(now.getHour()) //
                .TIMEM(now.getMinute()) //
                .TIMES(now.getSecond()) //
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/counter", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CounterResponse> counter(@RequestBody @NotNull @Valid RfidTagRequestBody requestBody) {
        log.info("Received mac address: {}", requestBody.MACADDR());
        log.info("Received RFID tag id: {}", requestBody.RFID());

        RfidTagId rfidTagId = RfidTagId.of(requestBody.RFID());
        MacAddress macAddress = MacAddress.of(requestBody.MACADDR());
        RfidReaderView rfidReader = this.rfidReaderService.getRfidReader(macAddress) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(macAddress));

        CounterResponse response = switch (rfidReader.type()) {
            case RfidReaderType.RFID_TAG_REGISTER -> registerRfidTag(rfidTagId);
            case RfidReaderType.RFID_TAG_ASSIGNER -> assignRfidTagOrGetUser(rfidTagId);
            default -> throw new IllegalStateException("Unexpected value: " + rfidReader.type());
        };

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/card", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceCardResponse> card(@RequestBody @NotNull @Valid DeviceCardRequestBody requestBody) {
        log.info("Received mac address: {}", requestBody.MACADDR());
        log.info("Received RFID tag id: {}", requestBody.RFID());

        RfidTagId rfidTagId = RfidTagId.of(requestBody.RFID());
        MacAddress macAddress = MacAddress.of(requestBody.MACADDR());
        RfidReaderView rfidReader = this.rfidReaderService.getRfidReader(macAddress) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(macAddress));

        //        if (rfidReader.type() != RfidReaderType.GATE_KEEPER) {
        //            throw new RuntimeException("TODO: Invalid type of RFID reader");
        //        }

        RfidTagView rfidTag = this.rfidTagService.getRfidTag(rfidTagId) //
                .orElseThrow(() -> ResourceNotFoundException.rfidTag(rfidTagId));

        CustomerId customerId = this.rfidTagAssignmentService.getRfidTagAssignment(rfidTag.id()) //
                .map(RfidTagAssignmentView::customerId) //
                .orElseThrow(() -> new RuntimeException("TODO: No customer assigned to RFID tag"));

        DeviceCardResponse response;
        if (rfidReader.type() == RfidReaderType.GATE_KEEPER_IN) {
            this.enterWorkshop.process(customerId);
            response = DeviceCardResponse.builder() //
                    .DEVUSECASE(RfidReaderType.GATE_KEEPER_IN.getKey()) //
                    .ERROR("") //
                    .STATE("END") //
                    .ICON("HI") //
                    .CUSTOMERNAME("Max Musterman") //
                    .CUSTOMERSTARTSTOP("3:00") //
                    .UNITS("5:00") //
                    .build();
        }
        else if (rfidReader.type() == RfidReaderType.GATE_KEEPER_OUT) {
            this.leaveWorkshop.process(customerId);
            response = DeviceCardResponse.builder() //
                    .DEVUSECASE(RfidReaderType.GATE_KEEPER_OUT.getKey()) //
                    .ERROR("") //
                    .STATE("END") //
                    .ICON("BYE") //
                    .CUSTOMERNAME("Max Musterman") //
                    .CUSTOMERSTARTSTOP("3:00") //
                    .UNITS("5:00") //
                    .build();
        }
        else {
            throw new RuntimeException("TODO: Invalid type of RFID reader");
        }

        return ResponseEntity.ok(response);
    }

    // ------------------------------------------------------------------------
    // helper

    private CounterResponse registerRfidTag(RfidTagId rfid) {
        Optional<RfidTagView> rfidTag = this.rfidTagService.getRfidTag(rfid);
        if (rfidTag.isPresent()) {
            return CounterResponse.registerError("RFID bereits vorhanden!");
        }
        else {
            CreateRfidTagCommand command = new CreateRfidTagCommand(rfid.getValue());
            this.rfidTagService.createRfidTag(command);

            return CounterResponse.registerOk();
        }
    }

    private CounterResponse assignRfidTagOrGetUser(RfidTagId rfidTagId) {
        // TODO: switch to ifPresentOrElse
        RfidTagAssignmentView assignment = this.rfidTagAssignmentService.getRfidTagAssignment(rfidTagId).orElse(null);
        if (isNull(assignment)) {
            return assignRfidTag(rfidTagId);
        }

        return getUser(assignment.customerId());
    }

    private CounterResponse assignRfidTag(RfidTagId rfidTagId) {
        try {
            this.assignRfidTagUseCase.process(rfidTagId);

            return CounterResponse.assignerOk();
        }
        catch (Exception e) {
            return CounterResponse.assignerError(e.getMessage());
        }
    }

    private CounterResponse getUser(CustomerId customerId) {
        CustomerView customer = this.customerService.getCustomer(customerId) //
                .orElseThrow(() -> ResourceNotFoundException.customer(customerId));

        String fullName = customer.firstName() + " " + customer.lastName();

        return CounterResponse.assignerError(fullName);
    }

}
