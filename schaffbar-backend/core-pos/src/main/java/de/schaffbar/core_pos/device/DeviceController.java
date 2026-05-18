package de.schaffbar.core_pos.device;

import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import de.schaffbar.core_pos.customer.CustomerService;
import de.schaffbar.core_pos.customer.CustomerViews.CustomerView;
import de.schaffbar.core_pos.device.DeviceApiModel.CounterResponse;
import de.schaffbar.core_pos.device.DeviceApiModel.DeviceCardRequestBody;
import de.schaffbar.core_pos.device.DeviceApiModel.DeviceCardResponse;
import de.schaffbar.core_pos.device.DeviceApiModel.InitRequestBody;
import de.schaffbar.core_pos.device.DeviceApiModel.InitResponse;
import de.schaffbar.core_pos.device.DeviceApiModel.RfidTagRequestBody;
import de.schaffbar.core_pos.rfid_reader.RfidReaderService;
import de.schaffbar.core_pos.rfid_reader.RfidReaderType;
import de.schaffbar.core_pos.rfid_reader.RfidReaderViews.RfidReaderView;
import de.schaffbar.core_pos.rfid_tag.RfidTagCommands.CreateRfidTagCommand;
import de.schaffbar.core_pos.rfid_tag.RfidTagService;
import de.schaffbar.core_pos.rfid_tag.RfidTagViews.RfidTagView;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentService;
import de.schaffbar.core_pos.rfid_tag_assignment.RfidTagAssignmentViews.RfidTagAssignmentView;
import de.schaffbar.core_pos.shared.exception.CustomerNotCertifiedForToolException;
import de.schaffbar.core_pos.shared.exception.CustomerNotInWorkshopException;
import de.schaffbar.core_pos.shared.exception.MaxToolUsageExceededException;
import de.schaffbar.core_pos.shared.exception.NoActiveWorkshopSessionFoundException;
import de.schaffbar.core_pos.shared.exception.NoActiveWorkshopUsageFoundException;
import de.schaffbar.core_pos.shared.exception.NoCustomerAssignedException;
import de.schaffbar.core_pos.shared.exception.ResourceNotFoundException;
import de.schaffbar.core_pos.shared.exception.UserAlreadyInWorkshopException;
import de.schaffbar.core_pos.shared.id.CustomerId;
import de.schaffbar.core_pos.shared.id.MacAddress;
import de.schaffbar.core_pos.shared.id.RfidReaderId;
import de.schaffbar.core_pos.shared.id.RfidTagId;
import de.schaffbar.core_pos.tool.ToolService;
import de.schaffbar.core_pos.tool.ToolViews.ToolView;
import de.schaffbar.core_pos.tool_usage.ToolUsageService;
import de.schaffbar.core_pos.use_case.CustomerAssignRfidTag;
import de.schaffbar.core_pos.use_case.EnterWorkshop;
import de.schaffbar.core_pos.use_case.LeaveWorkshop;
import de.schaffbar.core_pos.use_case.StartToolUsage;
import de.schaffbar.core_pos.use_case.StopToolUsage;
import de.schaffbar.core_pos.workshop_session.WorkshopSessionService;
import de.schaffbar.core_pos.workshop_session.WorkshopSessionViews.WorkshopSessionView;
import de.schaffbar.core_pos.workshop_usage.WorkshopUsageService;
import de.schaffbar.core_pos.workshop_usage.WorkshopUsageViews.WorkshopUsageView;
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

    private final @NonNull ToolService toolService;

    private final @NonNull ToolUsageService toolUsageService;

    private final @NonNull EnterWorkshop enterWorkshop;

    private final @NonNull LeaveWorkshop leaveWorkshop;

    private final @NonNull StartToolUsage startToolUsage;

    private final @NonNull StopToolUsage stopToolUsage;

    private final @NonNull CustomerAssignRfidTag assignRfidTagUseCase;

    private final @NonNull WorkshopSessionService workshopSessionService;

    private final @NonNull WorkshopUsageService workshopUsageService;

    @PostMapping(value = "/init", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InitResponse> init(@RequestBody @NotNull @Valid InitRequestBody requestBody) {
        log.info("Init RFID reader. Received mac address: {}", requestBody.MACADDR());

        MacAddress macAddress = MacAddress.of(requestBody.MACADDR());
        RfidReaderId id = this.rfidReaderService.getRfidReader(macAddress) //
                .map(RfidReaderView::id) //
                .orElseGet(() -> this.rfidReaderService.createRfidReader(macAddress));

        RfidReaderView rfidReader = this.rfidReaderService.getRfidReader(id) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(id));

        String startHttp = "";
        String devIp = "";
        String switchOn = "";
        String switchOff = "";

        if (rfidReader.type() == RfidReaderType.SWITCH_BOX) {
            Optional<ToolView> tool = this.toolService.getTool(rfidReader.id());
            if (tool.isPresent()) {
                ToolView t = tool.get();
                startHttp = "http://";
                devIp = Objects.toString(t.ipAddress(), "");
                switchOn = "/relay/0?" + Objects.toString(t.onCommand(), "");
                switchOff = "/relay/0?" + Objects.toString(t.offCommand(), "");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        InitResponse response = InitResponse.builder() //
                .STATE(isNull(rfidReader.type()) ? "ERROR" : "START") //
                .DEVNAME(getDeviceNme(rfidReader)) //
                .STARTHTTP(startHttp) //
                .DEVIP(devIp) //
                .SWITCHON(switchOn) //
                .SWITCHOFF(switchOff) //
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

        log.info("RFID reader init response: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/counter", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CounterResponse> counter(@RequestBody @NotNull @Valid RfidTagRequestBody requestBody) {
        RfidReaderView rfidReader = getRfidReader(MacAddress.of(requestBody.MACADDR()));

        RfidTagId rfidTagId = RfidTagId.of(requestBody.RFID());
        CounterResponse response = switch (rfidReader.type()) {
            case RfidReaderType.RFID_TAG_REGISTER -> registerRfidTag(rfidTagId);
            case RfidReaderType.RFID_TAG_ASSIGNER -> assignRfidTagOrGetUser(rfidTagId);
            default -> CounterResponse.unexpectedRfidReaderType("Unexpected RFID reader type: " + rfidReader.type());
        };

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/card", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeviceCardResponse> card(@RequestBody @NotNull @Valid DeviceCardRequestBody requestBody) {
        String devUseCase = requestBody.DEVUSECASE();
        RfidReaderView rfidReader;
        CustomerView customer;

        try {
            rfidReader = getRfidReader(MacAddress.of(requestBody.MACADDR()));
            customer = getCustomer(RfidTagId.of(requestBody.RFID()));
        }
        catch (ResourceNotFoundException e) {
            String message = switch (e.getResource()) {
                case RFID_READER -> "RFID Reader nicht erkannt";
                case RFID_TAG -> "RFID Tag nicht erkannt";
                case CUSTOMER -> "Kunde nicht erkannt";
                default -> "Unerwarteter Fehler";
            };

            log.error(message);
            return ResponseEntity.ok(DeviceCardResponse.errorNoUserRecognized(message, devUseCase));
        }
        catch (NoCustomerAssignedException e) {
            log.error("No customer assigned to RFID tag [id: {}]. Returning error response.", e.getMessage());
            return ResponseEntity.ok(DeviceCardResponse.errorNoUserRecognized("Kein Kunde für RFID Tag", devUseCase));
        }
        catch (Exception e) {
            log.error("An unexpected error occurred while processing RFID tag request", e);
            return ResponseEntity.ok(DeviceCardResponse.errorUnexpected("Unerwarteter Fehler", devUseCase));
        }

        try {
            DeviceCardResponse response = switch (rfidReader.type()) {
                case GATE_KEEPER_IN -> enterWorkshop(customer, rfidReader.type());
                case GATE_KEEPER_OUT -> leaveWorkshop(customer, rfidReader.type());
                case SWITCH_BOX -> toggleToolUsage(customer, rfidReader);
                default -> throw new IllegalStateException("Unexpected value for RFID reader type: " + rfidReader.type());
            };

            return ResponseEntity.ok(response);
        }
        catch (UserAlreadyInWorkshopException e) {
            log.error("User is already in workshop [id: {}]", rfidReader.id());
            return ResponseEntity.ok(DeviceCardResponse.errorNoAccess("Du schaffst schon", customer.getFullName(), devUseCase));
        }
        catch (NoActiveWorkshopUsageFoundException e) {
            log.error("No active workshop usage [id: {}]", rfidReader.id());
            return ResponseEntity.ok(DeviceCardResponse.errorNoAccess("Zwei mal Pause geht nicht", customer.getFullName(), devUseCase));
        }
        catch (NoActiveWorkshopSessionFoundException e) {
            log.error("No active workshop [id: {}]", rfidReader.id());
            return ResponseEntity.ok(DeviceCardResponse.errorNoAccess("Du warst nie im Werkstatt", customer.getFullName(), devUseCase));
        }
        catch (CustomerNotInWorkshopException e) {
            log.error("Customer is not in workshop [id: {}]", rfidReader.id());
            return ResponseEntity.ok(DeviceCardResponse.errorNoAccess("Du bist nicht in der Werkstatt", customer.getFullName(), devUseCase));
        }
        catch (MaxToolUsageExceededException e) {
            log.error("Max tool usage [id: {}]", rfidReader.id());
            return ResponseEntity.ok(DeviceCardResponse.errorNoAccess("Max. Werkzeuge erreicht", customer.getFullName(), devUseCase));
        }
        catch (CustomerNotCertifiedForToolException e) {
            log.error("Customer not certified for tool [id: {}]", rfidReader.id());
            return ResponseEntity.ok(DeviceCardResponse.errorNoAccess("Keine Einweisung", customer.getFullName(), devUseCase));
        }
        catch (Exception e) {
            log.error("An unexpected error occurred while processing RFID tag request. Message {}", e.getMessage());
            return ResponseEntity.ok(DeviceCardResponse.errorUnexpected("Unerwarteter Fehler", devUseCase));
        }
    }

    // ------------------------------------------------------------------------
    // helper

    private String getDeviceNme(RfidReaderView rfidReader) {
        if (isNull(rfidReader.type())) {
            return "ERROR";
        }

        return switch (rfidReader.type()) {
            case RFID_TAG_REGISTER -> "RFID Tag Register";
            case RFID_TAG_ASSIGNER -> "RFID Tag Assigner";
            case GATE_KEEPER_IN -> "Gate Keeper In";
            case GATE_KEEPER_OUT -> "Gate Keeper Out";
            case SWITCH_BOX -> "Switch Box";
        };
    }

    private RfidReaderView getRfidReader(MacAddress macAddress) {
        return this.rfidReaderService.getRfidReader(macAddress) //
                .orElseThrow(() -> ResourceNotFoundException.rfidReader(macAddress));
    }

    private CustomerView getCustomer(RfidTagId rfidTagId) {
        RfidTagView rfidTag = this.rfidTagService.getRfidTag(rfidTagId) //
                .orElseThrow(() -> ResourceNotFoundException.rfidTag(rfidTagId));

        CustomerId customerId = this.rfidTagAssignmentService.getRfidTagAssignment(rfidTag.id()) //
                .map(RfidTagAssignmentView::customerId) //
                .orElseThrow(() -> new NoCustomerAssignedException(rfidTagId));

        return this.customerService.getCustomer(customerId) //
                .orElseThrow(() -> ResourceNotFoundException.customer(customerId));
    }

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

        return getUser(assignment.customerId(), rfidTagId);
    }

    private CounterResponse assignRfidTag(RfidTagId rfidTagId) {
        try {
            this.assignRfidTagUseCase.process(rfidTagId);
            CustomerView customer = getCustomer(rfidTagId);

            return CounterResponse.assignerOk(customer.getFullName(), rfidTagId);
        }
        catch (ResourceNotFoundException e) {
            String message = switch (e.getResource()) {
                case RFID_READER -> "RFID Reader unbekannt";
                case RFID_TAG -> "RFID Tag unbekannt";
                case CUSTOMER -> "Kunde unbekannt";
                default -> "Unerwarteter Fehler";
            };

            return CounterResponse.assignerError(message);
        }
        catch (Exception e) {
            return CounterResponse.assignerError(e.getMessage());
        }
    }

    private CounterResponse getUser(CustomerId customerId, RfidTagId rfidTagId) {
        CustomerView customer = this.customerService.getCustomer(customerId) //
                .orElseThrow(() -> ResourceNotFoundException.customer(customerId));

        return CounterResponse.userQueryOk(customer.getFullName(), rfidTagId);
    }

    private DeviceCardResponse enterWorkshop(CustomerView customer, RfidReaderType type) {
        this.enterWorkshop.process(customer.id());

        List<WorkshopUsageView> workshopUsages = getWorkshopUsages(customer.id());
        long totalUnits = workshopUsages.stream() //
                .map(WorkshopUsageView::getUnitsUsed) //
                .filter(Objects::nonNull) //
                .reduce(0L, Long::sum);

        WorkshopUsageView lastWorkshopUsage = workshopUsages.stream() //
                .filter(usage -> isNull(usage.exitTime())) //
                .sorted(comparing(WorkshopUsageView::entryTime).reversed()) //
                .toList().getFirst();

        return DeviceCardResponse.enterOk(type, customer.getFullName(), totalUnits, lastWorkshopUsage.entryTime());
    }

    private DeviceCardResponse leaveWorkshop(CustomerView customer, RfidReaderType type) {
        this.leaveWorkshop.process(customer.id());

        List<WorkshopUsageView> workshopUsages = getWorkshopUsages(customer.id());
        long totalUnits = workshopUsages.stream() //
                .map(WorkshopUsageView::getUnitsUsed) //
                .filter(Objects::nonNull) //
                .reduce(0L, Long::sum);

        WorkshopUsageView lastWorkshopUsage = workshopUsages.stream() //
                .sorted(comparing(WorkshopUsageView::exitTime).reversed()) //
                .toList().getFirst();

        return DeviceCardResponse.leaveOk(type, customer.getFullName(), totalUnits, lastWorkshopUsage.entryTime(), lastWorkshopUsage.exitTime());
    }

    private DeviceCardResponse toggleToolUsage(CustomerView customer, RfidReaderView rfidReader) {
        ToolView tool = this.toolService.getTool(rfidReader.id()) //
                .orElseThrow(() -> new RuntimeException("No tool assigned to RFID reader [id: " + rfidReader.id() + "]"));

        //        boolean hasActiveUsage = this.toolUsageService.getActiveToolUsages(customer.id()).stream() //
        //                .anyMatch(usage -> usage.toolId().sameValueAs(tool.id()));
        boolean hasActiveUsage = this.toolUsageService.getActiveToolUsage(customer.id(), tool.id()).isPresent();
        if (hasActiveUsage) {
            this.stopToolUsage.process(customer.id(), tool.id());
            return DeviceCardResponse.stopToolUsage(rfidReader.type(), customer.getFullName(), tool.name());
        }
        else {
            this.startToolUsage.process(customer.id(), tool.id());
            return DeviceCardResponse.startToolUsage(rfidReader.type(), customer.getFullName(), tool.name());
        }

    }

    private List<WorkshopUsageView> getWorkshopUsages(CustomerId customerId) {
        WorkshopSessionView workshopSessionView = this.workshopSessionService.getOpenWorkshopSession(customerId) //
                .orElseThrow(() -> new RuntimeException("No open workshop session found after entering workshop")); // TODO: error handling

        return this.workshopUsageService.getWorkshopUsages(workshopSessionView.id());
    }

}
