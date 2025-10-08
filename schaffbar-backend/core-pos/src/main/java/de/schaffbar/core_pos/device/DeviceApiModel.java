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
            String ICON  //
    ) {

        public static CounterResponse assignerOk() {
            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_ASSIGNER.getKey()) //
                    .ERROR("") //
                    .STATE("END") //
                    .ICON("OK") //
                    .build();
        }

        public static CounterResponse registerOk() {
            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_REGISTER.getKey()) //
                    .ERROR("") //
                    .STATE("END") //
                    .ICON("OK") //
                    .build();
        }

        public static CounterResponse assignerError(String message) {
            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_ASSIGNER.getKey()) //
                    .ERROR(message) //
                    .STATE("END") //
                    .ICON("RFID") //
                    .build();
        }

        public static CounterResponse registerError(String message) {
            return CounterResponse.builder() //
                    .DEVUSECASE(RfidReaderType.RFID_TAG_REGISTER.getKey()) //
                    .ERROR(message) //
                    .STATE("END") //
                    .ICON("RFID") //
                    .build();
        }

    }

    // TODO: change access level to PRIVATE
    // @Builder(access = AccessLevel.PRIVATE)
    @Builder
    record DeviceCardResponse( //
            String DEVUSECASE,  //
            String ERROR,  //
            String STATE,  //
            String ICON,  //
            String CUSTOMERNAME,  //
            String CUSTOMERSTARTSTOP,  //
            String UNITS  //
    ) {}

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
