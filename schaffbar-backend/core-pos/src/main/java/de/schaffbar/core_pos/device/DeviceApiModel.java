package de.schaffbar.core_pos.device;

import de.schaffbar.core_pos.rfid_reader.RfidReaderType;
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
                    .ERROR("") //
                    .ICON("OK") //
                    .STATE("END") //
                    .build();
        }

        public static CounterResponse assignerOk() {
            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_ASSIGNER.getKey()) //
                    .ERROR("") //
                    .ICON("OK") //
                    .STATE("END") //
                    .CUSTOMERNAME("Piotr") // TODO: deliver this info
                    .RFID("123456") // TODO: deliver this info
                    .build();
        }

        public static CounterResponse userQueryOk(String customerFullName) {
            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_ASSIGNER.getKey()) //
                    .ERROR("") //
                    .CUSTOMERNAME(customerFullName) //
                    .build();
        }

        public static CounterResponse registerError(String message) {
            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_REGISTER.getKey()) //
                    .ERROR(message) //
                    .CUSTOMERNAME("") // TODO: why do we need this field in error case?
                    .build();
        }

        public static CounterResponse assignerError(String message) {
            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_ASSIGNER.getKey()) //
                    .ERROR(message) //
                    .CUSTOMERNAME("Test Customer") // TODO: why do we need this field in error case?
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

        public static DeviceCardResponse enterOk(String customerName) {
            return DeviceCardResponse.builder() //
                    .DEVUSECASE(RfidReaderType.GATE_KEEPER_IN.getKey()) //
                    .ICON("HI") //
                    .ERROR("") //
                    .STATE("END") //
                    .CUSTOMERNAME(customerName) //
                    .CUSTOMERSTARTSTOP("0:00") // TODO: deliver this info
                    .UNITS("0") // TODO: deliver this info
                    .build();
        }

        public static DeviceCardResponse leaveOk(String customerName) {
            return DeviceCardResponse.builder() //
                    .DEVUSECASE(RfidReaderType.GATE_KEEPER_OUT.getKey()) //
                    .ICON("BYE") //
                    .ERROR("") //
                    .STATE("END") //
                    .CUSTOMERNAME(customerName) //
                    .CUSTOMERSTARTSTOP("0:00") // TODO: deliver this info
                    .UNITS("0") // TODO: deliver this info
                    .build();
        }

        public static DeviceCardResponse errorNoUserRecognized(String message) {
            return DeviceCardResponse.builder() //
                    .ICON("NOREG") //
                    .ERROR(message) //
                    .UNITS("0") //
                    .build();
        }

        public static DeviceCardResponse errorNoAccess(String message, String customerName) {
            return DeviceCardResponse.builder() //
                    .ICON("STOP") //
                    .ERROR(message) //
                    .CUSTOMERNAME(customerName) //
                    .UNITS("0") //
                    .build();
        }

        public static DeviceCardResponse errorUnexpected(String message) {
            return DeviceCardResponse.builder() //
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
            String MACADDR, //
            String RFID //
    ) {}

}
