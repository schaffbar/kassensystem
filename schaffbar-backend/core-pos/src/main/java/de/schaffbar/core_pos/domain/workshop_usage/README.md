# WorkshopUsage

**Aggregate Root:** `WorkshopUsage`

**Identity:** `WorkshopUsageId` (UUID)

_Note: Code suggests considering renaming to `WorkshopSlot` or `UsageSlot`._

## Attributes

| Field               | Type      | Constraints           |
|---------------------|-----------|-----------------------|
| `id`                | `UUID`    | PK                    |
| `customerId`        | `UUID`    | `@NotNull`            |
| `workshopSessionId` | `UUID`    | `@NotNull`            |
| `entryTime`         | `Instant` | `@NotNull`            |
| `exitTime`          | `Instant` | optional, set on exit |
| `updatedAt`         | `Instant` | `@Version`            |

## Commands

| Operation      | Input                         |
|----------------|-------------------------------|
| Enter workshop | customerId, workshopSessionId |
| Leave workshop | customerId, workshopSessionId |

## Business Rules / Invariants

| #    | Rule                                                                            | Enforced in                                                                                   |
|------|---------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| WU-1 | A customer can have at most one active (no `exitTime`) workshop usage at a time | `WorkshopUsageService.enterWorkshop()` — checks for open usage via `findActiveByCustomerId()` |
| WU-2 | Customer must have an active usage to leave                                     | `WorkshopUsageService.leaveWorkshop()` — throws `NoActiveWorkshopUsageFoundException`         |
| WU-3 | Duration is computed as `exitTime - entryTime` (truncated to seconds)           | `WorkshopUsage.getDuration()` — returns `null` if either time is missing                      |
| WU-4 | A workshop session must exist (OPEN) before entering                            | `EnterWorkshop` use case — creates session if not present                                     |
| WU-5 | Active tool usages are stopped when leaving the workshop                        | `LeaveWorkshop` use case — calls `ToolUsageService.stopAllUsagesForCustomer()`                |
| WU-6 | Customer must have a valid certificate to enter                                 | **TODO** — noted in `EnterWorkshop` use case                                                  |

## Domain Events

| Event                    | Payload        |
|--------------------------|----------------|
| `WORKSHOP_USAGE_ENTERED` | Usage snapshot |
| `WORKSHOP_USAGE_LEFT`    | Usage snapshot |
