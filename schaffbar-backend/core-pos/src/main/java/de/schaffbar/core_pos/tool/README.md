# Tool

**Aggregate Root:** `Tool`

**Identity:** `ToolId` (UUID)

## Attributes

| Field                      | Type                       | Constraints                                          |
|----------------------------|----------------------------|------------------------------------------------------|
| `id`                       | `UUID`                     | PK                                                   |
| `name`                     | `String`                   | `@NotBlank`, unique                                  |
| `description`              | `String`                   | optional                                             |
| `area`                     | `ToolArea`                 | `@NotNull` enum (`HOLZ`, `METALL`, `KREATIV`, `FABLAB`) |
| `certificationRequirement` | `CertificationRequirement` | `@NotNull` enum (`YELLOW`, `RED`)                    |
| `rfidReaderId`             | `UUID`                     | optional, FK-like reference                          |
| `wlanRelaisType`           | `WlanRelaisType`           | optional enum (`SHELLY_1`, `SHELLY_2`, `SHELLY_PRO`) |
| `ipAddress`                | `String`                   | required when `wlanRelaisType` is set                |
| `httpStartCommand`         | `String`                   | auto-derived from `WlanRelaisType` template          |
| `onCommand`                | `String`                   | auto-derived from `WlanRelaisType` template          |
| `offCommand`               | `String`                   | auto-derived from `WlanRelaisType` template          |
| `createdAt`                | `Instant`                  | `@NotNull`                                           |
| `updatedAt`                | `Instant`                  | `@Version`                                           |

## Commands

| Command                 | Fields                                             |
|-------------------------|----------------------------------------------------|
| `CreateToolCommand`     | name, description, area, certificationRequirement  |
| `UpdateToolCommand`     | name, description, area, certificationRequirement  |
| `SetWlanRelaisCommand`  | wlanRelaisType, ipAddress                          |

## Business Rules / Invariants

| #    | Rule                                                                                                                                    | Enforced in                                                          |
|------|-----------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------|
| T-1  | `name` must not be blank                                                                                                                | Bean Validation                                                      |
| T-2  | Tool name must be unique across all tools                                                                                               | `ToolService.createTool()`, `ToolService.updateTool()`               |
| T-3  | An RFID reader can only be assigned to one tool at a time                                                                               | `ToolService.assignRfidReader()`                                     |
| T-4  | An IP address can only be used by one tool at a time                                                                                    | `ToolService.setWlanRelais()`                                        |
| T-5  | When `wlanRelaisType` is set, `ipAddress` is required                                                                                   | `SetWlanRelaisCommand` — Bean Validation `@NotNull`                  |
| T-6  | WLAN-Relais commands (`httpStartCommand`, `onCommand`, `offCommand`) are derived from the `WlanRelaisType` template — not user-settable | `Tool.setWlanRelais()`                                               |
| T-7  | Clearing WLAN-Relais resets all relais-related fields to null                                                                           | `Tool.clearWlanRelais()`                                             |
| T-8  | If assigning an RFID reader that is already assigned to the same tool → no-op (idempotent)                                              | `ToolService.assignRfidReader()`                                     |
| T-9  | Only RFID readers of type `SWITCH_BOX` can be assigned to tools                                                                         | `ToolAssignRfidReader` use case                                      |
| T-10 | `area` and `certificationRequirement` are mandatory and set at creation                                                                 | Bean Validation `@NotNull`                                           |
| T-11 | `area` and `certificationRequirement` can only be changed together with name and description via `UpdateToolCommand`                    | `Tool.update()`, `UpdateToolCommand`                                 |
| T-12 | When `certificationRequirement` is `RED`, tool usage requires active certification                                                      | `StartToolUsage` use case                                            |
| T-13 | When `certificationRequirement` is `YELLOW`, tool usage is allowed without certification (advisory only)                                | `StartToolUsage` use case                                            |

## Domain Events

| Event                         | Payload                                             |
|-------------------------------|-----------------------------------------------------|
| `TOOL_CREATED`                | Full tool snapshot (incl. area, certReq)            |
| `TOOL_UPDATED`                | Updated name, description, area, certificationReq   |
| `TOOL_WLAN_RELAIS_SET`        | WLAN-Relais config (type, ipAddress)                |
| `TOOL_WLAN_RELAIS_CLEARED`    | toolId                                              |
| `TOOL_RFID_READER_ASSIGNED`   | toolId, rfidReaderId                                |
| `TOOL_RFID_READER_CLEARED`    | toolId                                              |
| `TOOL_INSTRUCTORS_ADDED`      | toolId, instructorIds                               |
| `TOOL_INSTRUCTORS_REMOVED`    | toolId, instructorIds                               |
| `TOOL_DELETED`                | toolId                                              |
