# RfidTagAssignmentHistory

**Aggregate Root:** `RfidTagAssignmentHistory`

**Identity:** `RfidTagAssignmentId` (UUID) — _Note: FIXME in code suggests renaming to `RfidTagAssignmentHistoryId`_

## Attributes

| Field              | Type                    | Constraints                  |
|--------------------|-------------------------|------------------------------|
| `id`               | `UUID`                  | PK                           |
| `customerId`       | `UUID`                  | `@NotNull`                   |
| `rfidTagId`        | `String`                | `@NotNull`                   |
| `assignmentType`   | `RfidTagAssignmentType` | `@NotNull`                   |
| `assignmentDate`   | `Instant`               | `@NotNull`, `@Past`          |
| `unassignmentDate` | `Instant`               | `@NotNull`, `@PastOrPresent` |
| `updatedAt`        | `Instant`               | `@Version`                   |

## Business Rules / Invariants

| #     | Rule                                                                     | Enforced in                                          |
|-------|--------------------------------------------------------------------------|------------------------------------------------------|
| TAH-1 | History is created from a previously active (ASSIGNED) RfidTagAssignment | `RfidTagAssignmentHistory.of(RfidTagAssignmentView)` |
| TAH-2 | `assignmentDate` must be in the past                                     | `@Past` validation                                   |
| TAH-3 | `unassignmentDate` must be in the past or present                        | `@PastOrPresent` validation                          |
| TAH-4 | History records are immutable — no update commands                       | No command methods                                   |

## Domain Events

_None — this is a projection / audit log aggregate._
