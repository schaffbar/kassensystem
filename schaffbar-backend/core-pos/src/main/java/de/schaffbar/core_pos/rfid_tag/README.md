# RfidTag

**Aggregate Root:** `RfidTag`

**Identity:** `RfidTagId` (String — hardware ID)

## Attributes

| Field       | Type      | Constraints                    |
|-------------|-----------|--------------------------------|
| `id`        | `String`  | PK (hardware tag ID)           |
| `active`    | `boolean` | defaults to `true` on creation |
| `createdAt` | `Instant` | `@NotNull`                     |
| `updatedAt` | `Instant` | `@Version`                     |

## Commands

| Command                | Fields             |
|------------------------|--------------------|
| `CreateRfidTagCommand` | rfidTagId (String) |

## Business Rules / Invariants

| #    | Rule                                 | Enforced in                                     |
|------|--------------------------------------|-------------------------------------------------|
| RT-1 | RFID Tag ID must not be blank        | `@NotBlank` on `CreateRfidTagCommand.rfidTagId` |
| RT-2 | Newly created tags are always active | `RfidTag.of()` sets `active = true`             |

## Domain Events

| Event              | Payload           |
|--------------------|-------------------|
| `RFID_TAG_CREATED` | Full tag snapshot |
| `RFID_TAG_DELETED` | rfidTagId         |
