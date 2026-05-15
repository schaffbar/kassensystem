# RfidTagAssignment

**Aggregate Root:** `RfidTagAssignment`

**Identity:** `RfidTagAssignmentId` (UUID)

## Attributes

| Field            | Type                      | Constraints                                         |
|------------------|---------------------------|-----------------------------------------------------|
| `id`             | `UUID`                    | PK                                                  |
| `customerId`     | `UUID`                    | `@NotNull`, `@Column(unique = true)`                |
| `assignmentType` | `RfidTagAssignmentType`   | `@NotNull` (`FIXED` or `TEMPORARY`)                 |
| `status`         | `RfidTagAssignmentStatus` | `@NotNull` (`WAITING_FOR_ASSIGNMENT` or `ASSIGNED`) |
| `rfidTagId`      | `String`                  | `@Column(unique = true)`, set upon assignment       |
| `assignmentDate` | `Instant`                 | set upon assignment                                 |
| `updatedAt`      | `Instant`                 | `@Version`                                          |

**Lifecycle:** `WAITING_FOR_ASSIGNMENT` → `ASSIGNED` → _(deleted on unassign)_

## Commands

| Operation          | Input                      |
|--------------------|----------------------------|
| Request assignment | customerId, assignmentType |
| Assign RFID tag    | rfidTagId                  |
| Unassign           | customerId                 |

## Business Rules / Invariants

| #    | Rule                                                                                    | Enforced in                                                                                               |
|------|-----------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| TA-1 | A customer can have at most one active RFID tag assignment                              | `@Column(unique = true)` on `customerId` + check in `RfidTagAssignmentService.requestRfidTagAssignment()` |
| TA-2 | An RFID tag can be assigned to at most one customer                                     | `@Column(unique = true)` on `rfidTagId` + check in `RfidTagAssignmentService.assignRfidTag()`             |
| TA-3 | Only one pending assignment (`WAITING_FOR_ASSIGNMENT`) may exist at a time              | `RfidTagAssignmentService.requestRfidTagAssignment()`                                                     |
| TA-4 | Assignment can only happen when status is `WAITING_FOR_ASSIGNMENT`                      | **TODO** — noted in `RfidTagAssignment.assignRfidTag()`                                                   |
| TA-5 | Only `ASSIGNED` assignments can be unassigned                                           | `RfidTagAssignmentService.unassignRfidTag()` filters by `isAssigned()`                                    |
| TA-6 | On unassign, the assignment record is deleted (not soft-deleted) and history is created | `CustomerUnassignRfidTag` use case                                                                        |
| TA-7 | Customer must exist before requesting assignment                                        | `CustomerRequestRfidTagAssignment` use case                                                               |
| TA-8 | RFID tag must exist before it can be assigned                                           | `CustomerAssignRfidTag` use case                                                                          |

## Domain Events

| Event                           | Payload             |
|---------------------------------|---------------------|
| `RFID_TAG_ASSIGNMENT_REQUESTED` | Assignment snapshot |
| `RFID_TAG_ASSIGNED`             | Assignment snapshot |
| `RFID_TAG_UNASSIGNED`           | Assignment snapshot |
