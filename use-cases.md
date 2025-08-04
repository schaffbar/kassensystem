# Use case - Register RFID-reader (add-rfid-tag, gate-keeper, counter)

- [rfid-reader] [emtpy] send command DeviceInitRequest(mac address)
- [backend] rfid-reader unknown -> create rfid-reader with mac address
- [portal] [admin] set type to ADD_TOKEN, GATE_KEEPER or COUNTER and probably other information
- [rfid-reader] [empty] send command DeviceInitRequest(mac address)
- [backend] rfid-reader already in database -> return rfid-reader configuration

## Use case - Register RFID-tag

- [admin] makes beep
- [rfid-reader] [add-rfid-tag] send command CounterCardRequest(mac address, tag-id)
- [backend] check mac address (else excpetion) and type of rfid-reader (expetion if not add-rfid-tag)
- [backend] tag-id unknown -> create rfid-tag -> else exception rfid-tag already registered

# Use case - Register customer and assign temporary rfid-tag (fixed rfid-tag later)

- [portal] [thekenheld] create customer
- [portal] [thekenheld] request rfid-tag assignment
- [rfid-reader] [counter] send command CounterCardRequest(mac address, tag-id)
- [backend] check mac address (else excpetion) and type of rfid-reader (expetion if not counter)
- [backend] assign rfid-tag to customer

# Use case - Register switchbox

- [portal] [admin] create tool
- [portal] [admin] set wlan-relais configuration

- [rfid-reader] [emtpy] send command DeviceInitRequest(mac address)
- [backend] rfid-reader unknown -> create rfid-reader with mac address
- [portal] [admin] set type to SWITCH_BOX and probably other information
- [portal] [admin] assign rfid-reader to tool

- [rfid-reader] [emtpy] send command DeviceInitRequest(mac address)
- [backend] rfid-reader already in database -> return rfid-reader configuration together with wlan-relais configuration
