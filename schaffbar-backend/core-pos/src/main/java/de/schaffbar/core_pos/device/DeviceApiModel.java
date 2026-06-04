package de.schaffbar.core_pos.device;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import de.schaffbar.core_pos.domain.rfid_reader.RfidReaderType;
import de.schaffbar.core_pos.shared.id.RfidTagId;
import lombok.AccessLevel;
import lombok.Builder;

public interface DeviceApiModel {

    // ------------------------------------------------------------------------
    // response

    // TODO: change access level to PRIVATE
    // @Builder(access = AccessLevel.PRIVATE)
    @Builder
    record InitResponse( //
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

    @Builder(access = AccessLevel.PRIVATE)
    record CounterResponse( //
            String DEVUSECASE,  //
            String ERROR,  //
            String STATE,  //
            String CUSTOMERNAME,  //
            String RFID,  //
            String ICON  //
    ) {

        public static CounterResponse registerOk() {
            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_REGISTER.getKey()) //
                    .STATE("END") //
                    .ERROR("") //
                    .ICON("OK") //
                    .build();
        }

        public static CounterResponse assignerOk(String customerFullName, RfidTagId rfidTagId) {
            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_ASSIGNER.getKey()) //
                    .STATE("END") //
                    .ERROR("") //
                    .ICON("OK") //
                    .CUSTOMERNAME(customerFullName) //
                    .RFID(rfidTagId.getValue()) //
                    .build();
        }

        public static CounterResponse userQueryOk(String customerFullName, RfidTagId rfidTagId) {
            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_ASSIGNER.getKey()) //
                    .STATE("END") //
                    .ERROR("") //
                    .ICON("OK") //
                    .CUSTOMERNAME(customerFullName) //
                    .RFID(rfidTagId.getValue()) //
                    .build();
        }

        public static CounterResponse registerError(String message) {
            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_REGISTER.getKey()) //
                    .STATE("END") //
                    .ERROR(message) //
                    .CUSTOMERNAME("Error") //
                    .build();
        }

        public static CounterResponse assignerError(String message) {
            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_ASSIGNER.getKey()) //
                    .STATE("END") //
                    .ERROR(message) //
                    .CUSTOMERNAME("Error") //
                    .build();
        }

        public static CounterResponse unexpectedRfidReaderType(String message) {
            return CounterResponse.builder() //
                    .DEVUSECASE("") //
                    .STATE("END") //
                    .ERROR(message) //
                    .CUSTOMERNAME("Error") //
                    .build();
        }

    }

    @Builder(access = AccessLevel.PRIVATE)
    record DeviceCardResponse( //
            String DEVUSECASE,  //
            String ERROR,  //
            String STATE,  //
            String ICON,  //
            String CUSTOMERNAME,  //
            String CUSTOMERSTARTSTOP,  //
            String UNITS  //
    ) {

        public static DeviceCardResponse enterOk(RfidReaderType type, String customerName, long totalUnits, Instant lastEntryTime) {
            LocalTime time = lastEntryTime.atZone(ZoneId.systemDefault()).toLocalTime();
            String entryTime = time.format(DateTimeFormatter.ofPattern("HH:mm"));

            return DeviceCardResponse.builder() //
                    .DEVUSECASE(type.getKey()) //
                    .ICON("HI") //
                    .ERROR("") //
                    .STATE("END") //
                    .CUSTOMERNAME(customerName) //
                    .CUSTOMERSTARTSTOP(entryTime) //
                    .UNITS(String.valueOf(totalUnits)) //
                    .build();
        }

        public static DeviceCardResponse leaveOk(RfidReaderType type, String customerName, long totalUnits, Instant lastEntryTime, Instant lastExitTime) {
            LocalTime entryTime = lastEntryTime.atZone(ZoneId.systemDefault()).toLocalTime();
            String entryTimeString = entryTime.format(DateTimeFormatter.ofPattern("HH:mm"));

            LocalTime exitTime = lastExitTime.atZone(ZoneId.systemDefault()).toLocalTime();
            String exitTimeString = exitTime.format(DateTimeFormatter.ofPattern("HH:mm"));

            return DeviceCardResponse.builder() //
                    .DEVUSECASE(type.getKey()) //
                    .ICON("BYE") //
                    .ERROR("") //
                    .STATE("END") //
                    .CUSTOMERNAME(customerName) //
                    .CUSTOMERSTARTSTOP(entryTimeString + " - " + exitTimeString) //
                    .UNITS(String.valueOf(totalUnits)) //
                    .build();
        }

        public static DeviceCardResponse startToolUsage(RfidReaderType type, String customerName, String toolName) {
            return DeviceCardResponse.builder() //
                    .DEVUSECASE(type.getKey()) //
                    .ICON("") //
                    .ERROR("") //
                    .STATE("WORKING") //
                    .CUSTOMERNAME(customerName) //
                    .CUSTOMERSTARTSTOP(toolName) //
                    .UNITS("") //
                    .build();
        }

        public static DeviceCardResponse stopToolUsage(RfidReaderType type, String customerName, String toolName) {
            return DeviceCardResponse.builder() //
                    .DEVUSECASE(type.getKey()) //
                    .ICON("BYE") //
                    .ERROR("") //
                    .STATE("END") //
                    .CUSTOMERNAME(customerName) //
                    .CUSTOMERSTARTSTOP(toolName) //
                    .UNITS("") //
                    .build();
        }

        public static DeviceCardResponse errorNoUserRecognized(String message, String devUseCase) {
            return DeviceCardResponse.builder() //
                    .DEVUSECASE(devUseCase) //
                    .STATE("END") //
                    .ICON("NOREG") //
                    .ERROR(message) //
                    .UNITS("0") //
                    .build();
        }

        public static DeviceCardResponse errorNoAccess(String message, String customerName, String devUseCase) {
            return DeviceCardResponse.builder() //
                    .DEVUSECASE(devUseCase) //
                    .STATE("END") //
                    .ICON("STOP") //
                    .ERROR(message) //
                    .CUSTOMERNAME(customerName) //
                    .UNITS("0") //
                    .build();
        }

        public static DeviceCardResponse errorUnexpected(String message, String devUseCase) {
            return DeviceCardResponse.builder() //
                    .DEVUSECASE(devUseCase) //
                    .STATE("END") //
                    .ICON("NOREG") //
                    .ERROR(message) //
                    .UNITS("0") //
                    .build();
        }

    }

    // ------------------------------------------------------------------------
    // request body

    @Builder(access = AccessLevel.PRIVATE)
    record InitRequestBody( //
            String MACADDR //
    ) {}

    @Builder(access = AccessLevel.PRIVATE)
    record RfidTagRequestBody( //
            String MACADDR, //
            String RFID //
    ) {}

    @Builder(access = AccessLevel.PRIVATE)
    record DeviceCardRequestBody( //
            String DEVUSECASE, //
            String MACADDR, //
            String RFID //
    ) {}

}
