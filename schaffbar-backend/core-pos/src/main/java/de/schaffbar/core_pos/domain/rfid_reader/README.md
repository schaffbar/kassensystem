# RfidReader

**Aggregate Root:** `RfidReader`

**Identity:** `RfidReaderId` (UUID)

## Attributes

| Field        | Type             | Constraints                           |
|--------------|------------------|---------------------------------------|
| `id`         | `UUID`           | PK                                    |
| `macAddress` | `String`         | `@NotBlank`, `@Column(unique = true)` |
| `type`       | `RfidReaderType` | optional enum                         |
| `name`       | `String`         | optional                              |
| `socketName` | `String`         | optional                              |
| `createdAt`  | `Instant`        | `@NotNull`                            |
| `updatedAt`  | `Instant`        | `@Version`                            |

**Enum:** `RfidReaderType`

| Value               | Code | Purpose                                  |
|---------------------|------|------------------------------------------|
| `RFID_TAG_REGISTER` | `A`  | Registers new RFID tags                  |
| `RFID_TAG_ASSIGNER` | `C`  | Assigns RFID tags to customers           |
| `GATE_KEEPER_IN`    | `GI` | Workshop entry gate                      |
| `GATE_KEEPER_OUT`   | `GO` | Workshop exit gate                       |
| `SWITCH_BOX`        | `S`  | Controls tool power (paired with Tool)   |

## Commands

| Command                       | Fields                                                         |
|-------------------------------|----------------------------------------------------------------|
| _(Creation via MacAddress)_   | macAddress (`MacAddress` value object)                         |
| `UpdateRfidReaderCommand`     | id (`RfidReaderId`), type (`RfidReaderType`), name, socketName |
| `ChangeRfidReaderTypeCommand` | id (`RfidReaderId`), type (`RfidReaderType`)                   |

## Business Rules / Invariants

| #   | Rule                                                                                             | Enforced in                                                              |
|-----|--------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------|
| R-1 | `macAddress` must be unique across all RFID readers                                              | DB unique constraint, **TODO** in `RfidReaderService.createRfidReader()` |
| R-2 | `macAddress` must not be blank                                                                   | Bean Validation                                                          |
| R-3 | The type of an RFID reader of type `SWITCH_BOX` cannot be changed while a tool is assigned to it | `ChangeRfidReaderType` use case                                          |

## Domain Events

| Event                      | Payload              |
|----------------------------|----------------------|
| `RFID_READER_CREATED`      | Full reader snapshot |
| `RFID_READER_UPDATED`      | Updated fields       |
| `RFID_READER_TYPE_CHANGED` | rfidReaderId, type   |
| `RFID_READER_DELETED`      | rfidReaderId         |
