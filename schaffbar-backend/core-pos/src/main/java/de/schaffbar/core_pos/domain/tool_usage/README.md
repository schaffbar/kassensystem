# ToolUsage

**Aggregate Root:** `ToolUsage`

**Identity:** `ToolUsageId` (UUID)

## Attributes

| Field               | Type      | Constraints                |
|---------------------|-----------|----------------------------|
| `id`                | `UUID`    | PK                         |
| `customerId`        | `UUID`    | `@NotNull`                 |
| `toolId`            | `UUID`    | `@NotNull`                 |
| `workshopSessionId` | `UUID`    | `@NotNull`                 |
| `startTime`         | `Instant` | `@NotNull`                 |
| `endTime`           | `Instant` | optional, set when stopped |
| `updatedAt`         | `Instant` | `@Version`                 |

## Commands

| Command                 | Fields                                |
|-------------------------|---------------------------------------|
| `StartToolUsageCommand` | customerId, toolId, workshopSessionId |

## Business Rules / Invariants

| #     | Rule                                                                                   | Enforced in                                                                                             |
|-------|----------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| TU-1  | A customer can use at most 2 tools simultaneously                                      | `ToolUsageService.startUsage()` — checks count via `countActiveByCustomerId()`                          |
| TU-2  | A customer can only use tools when actively in the workshop (has active WorkshopUsage)  | `StartToolUsage` use case — calls `verifyCustomerIsInWorkshop()`                                        |
| TU-3  | A customer cannot start the same tool twice simultaneously                              | `ToolUsageService.startUsage()` — checks via `findActiveByCustomerIdAndToolId()`                        |
| TU-4  | When a customer leaves the workshop, all active tool usages are automatically stopped   | `LeaveWorkshop` use case — calls `ToolUsageService.stopAllUsagesForCustomer()`                          |
| TU-5  | Tool on/off detection happens via RFID reader (type SWITCH_BOX) assigned to the tool    | `DeviceController` — resolves tool via `ToolService.getTool(RfidReaderId)` and toggles start/stop       |
| TU-6  | Customer identification happens via RFID tag assignment                                 | `DeviceController` — resolves customer via `RfidTagAssignmentService`                                   |
| TU-7  | Duration is computed as `endTime - startTime` (truncated to seconds)                    | `ToolUsage.getDuration()` — returns `null` if either time is missing                                    |
| TU-8  | Customer must have an open workshop session to start a tool usage                       | `StartToolUsage` use case — checks for active session                                                   |
| TU-9  | A tool can only be used by one customer at a time                                       | `ToolUsageService.startUsage()` — checks via `findActiveByToolId()`, throws `ToolAlreadyInUseException` |
| TU-10 | Customer must have an active ToolCertification for the tool                             | `StartToolUsage` use case — calls `ToolCertificationService.hasActiveCertification()`                   |

## Domain Events

| Event                | Payload             |
|----------------------|---------------------|
| `TOOL_USAGE_STARTED` | Tool usage snapshot |
| `TOOL_USAGE_STOPPED` | Tool usage snapshot |
