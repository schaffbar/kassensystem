# Schaffbar — Documentation

## Table of Contents

1. [Domain Overview](#1-domain-overview)
2. [Aggregate Structure Pattern](#2-aggregate-structure-pattern)
3. [Aggregate Catalog](#3-aggregate-catalog)
    - [Customer](#31-customer)
    - [Tool](#32-tool)
    - [RfidReader](#33-rfidreader)
    - [RfidTag](#34-rfidtag)
    - [RfidTagAssignment](#35-rfidtagassignment)
    - [RfidTagAssignmentHistory](#36-rfidtagassignmenthistory)
    - [WorkshopSession](#37-workshopsession)
    - [WorkshopUsage](#38-workshopusage)
    - [ToolUsage](#39-toolusage)
4. [Use Cases (Application Services)](#4-use-cases-application-services)
5. [Read Models (Query Services)](#5-read-models-query-services)
6. [Value Objects](#6-value-objects)
7. [Domain Events](#7-domain-events)

---

## 1. Domain Overview

The **Schaffbar Core-POS** system manages a **workshop** (Werkstatt) where customers can enter, use tools, and be billed
for their time. The core domain revolves around:

- **Customer management** — personal data, contact info, addresses
- **RFID infrastructure** — readers, tags, and their assignment to customers
- **Tool management** — workshop tools with optional WLAN-Relais (Shelly) control and RFID reader pairing
- **Tool usage tracking** — tracks which customer uses which tool, for billing and safety enforcement
- **Workshop access tracking** — sessions (billing units) and usages (individual entry/exit time slots)
- **Workshop dashboard** — a read model showing currently active users

### Bounded Context

All aggregates live in a single bounded context: **Core-POS**. Cross-aggregate coordination is handled through **Use
Cases** (application-level services in the `use_case` package) that orchestrate calls across multiple domain services.

### Event-Driven Architecture

Every state mutation produces **`SchaffbarEvent`** instances that are persisted to a **transactional outbox** (
`outbox_event` table) within the same transaction. This enables reliable event publishing for downstream consumers.

---

## 2. Aggregate Structure Pattern

Every aggregate in this project follows a consistent internal structure. This section documents the pattern so each
module is easy to understand and extend.

```
<aggregate_name>/
│
├── <Aggregate>.java              # Aggregate Root (Entity)
├── <Aggregate>Commands.java      # Command records (input DTOs)
├── <Aggregate>Service.java       # Domain/Application Service
├── <Aggregate>Repository.java    # Spring Data JPA Repository
├── <Aggregate>Views.java         # MapStruct-based read-only projections (View records)
├── <Aggregate>EventFactory.java  # Static factory for creating SchaffbarEvents
├── <Aggregate>PayloadMapper.java # MapStruct mapper: Entity → Event Payload
├── <Status/Type Enums>.java      # Status or type enumerations (if any)
├── <Value Objects>.java          # Embedded value objects (if any, e.g. CustomerAddress)
│
└── web/
    ├── <Aggregate>Controller.java   # REST Controller
    └── <Aggregate>ApiModel.java     # API request/response models
```

### Component Responsibilities

| Component                 | Responsibility                                                                                                                                                                                                                    |
|---------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Aggregate Root**        | Encapsulates state + invariants. All mutations go through command methods that return `List<SchaffbarEvent>`. Static `of(...)` factory for creation returns a `*WithEvents` record containing the entity and its creation events. |
| **Commands**              | Immutable Java `record` types with Bean Validation annotations. Grouped in a `*Commands` interface.                                                                                                                               |
| **Service**               | Validates existence, enforces uniqueness constraints (cross-aggregate checks), delegates to aggregate root, persists entity + outbox events in a single `@Transactional` boundary.                                                |
| **Repository**            | Spring Data `JpaRepository` with custom query methods. Package-private.                                                                                                                                                           |
| **Views**                 | Read-only `record` projections mapped via MapStruct. Exposes a singleton `MAPPER` instance.                                                                                                                                       |
| **EventFactory**          | Creates `SchaffbarEvent` for each event type by mapping the entity to a payload via `PayloadMapper`, validating the payload, and wrapping it.                                                                                     |
| **PayloadMapper**         | MapStruct interface that maps from the aggregate root entity to specific event payload records.                                                                                                                                   |
| **Controller / ApiModel** | REST layer — maps HTTP requests to commands and calls service or use case.                                                                                                                                                        |

### Aggregate Root Internal Structure

Every aggregate root follows the same code layout:

```java
class

<Aggregate> {

    // --- Fields (JPA @Id, domain attributes, @Version) ---

    // --- static constructor ---
    // static <WithEvents> of(<CreateCommand>) { ... }

    // --- query ---
    // getId() returns typed Value Object
    // other derived query methods

    // --- command ---
    // List<SchaffbarEvent> update(...) { ... }
    // List<SchaffbarEvent> <domainAction>(...) { ... }

    // --- helper ---
    // record <WithEvents>(<Aggregate>, List<SchaffbarEvent>) {}
}
```

**Key conventions:**

- **No public setters** — state changes only through named command methods
- **Every command method returns `List<SchaffbarEvent>`** — caller (Service) persists them to the outbox
- **Static factory `of(...)`** returns a `*WithEvents` record (entity + events)
- **`@Version` field** (`Instant updatedAt`) for optimistic locking
- **IDs stored as raw types** (`UUID`, `String`) internally but exposed as **Value Objects** via getter methods

---

## 3. Aggregate Catalog

### 3.1 Customer

**Package:** `de.schaffbar.core_pos.customer`

**Aggregate Root:** `Customer`

**Identity:** `CustomerId` (UUID)

#### Attributes

| Field         | Type              | Constraints               |
|---------------|-------------------|---------------------------|
| `id`          | `UUID`            | PK                        |
| `firstName`   | `String`          | `@NotBlank`               |
| `lastName`    | `String`          | `@NotBlank`               |
| `dateOfBirth` | `LocalDate`       | `@NotNull`                |
| `clubMember`  | `boolean`         | —                         |
| `email`       | `String`          | `@NotBlank`               |
| `phone`       | `String`          | optional                  |
| `address`     | `CustomerAddress` | `@NotNull`, `@Embeddable` |
| `createdAt`   | `Instant`         | `@NotNull`                |
| `updatedAt`   | `Instant`         | `@Version`                |

**Value Object:** `CustomerAddress` (embedded)

- `addressLine1` (`@NotBlank`), `addressLine2` (optional), `postalCode` (`@NotBlank`), `city` (`@NotBlank`), `country` (
  `@NotBlank`)

#### Commands

| Command                        | Fields                                                                                                            |
|--------------------------------|-------------------------------------------------------------------------------------------------------------------|
| `CreateCustomerCommand`        | firstName, lastName, dateOfBirth, clubMember, email, phone, addressLine1, addressLine2, postalCode, city, country |
| `UpdateCustomerCommand`        | id (`CustomerId`), firstName, lastName, dateOfBirth, clubMember                                                   |
| `UpdateCustomerContactCommand` | id (`CustomerId`), email, phone                                                                                   |
| `UpdateCustomerAddressCommand` | id (`CustomerId`), addressLine1, addressLine2, postalCode, city, country                                          |

#### Business Rules / Invariants

| #   | Rule                                                    | Enforced in                                            |
|-----|---------------------------------------------------------|--------------------------------------------------------|
| C-1 | `firstName`, `lastName`, `email` must not be blank      | Bean Validation on Entity + Commands                   |
| C-2 | `dateOfBirth` must not be null                          | Bean Validation                                        |
| C-3 | Address must always be present and valid                | `@NotNull` on embedded `CustomerAddress`               |
| C-4 | Customer must not be deleted if assigned to an RFID tag | **TODO** — noted in `CustomerService.deleteCustomer()` |

#### Domain Events

| Event                      | Payload                |
|----------------------------|------------------------|
| `CUSTOMER_CREATED`         | Full customer snapshot |
| `CUSTOMER_UPDATED`         | Updated base fields    |
| `CUSTOMER_CONTACT_CHANGED` | email, phone           |
| `CUSTOMER_ADDRESS_CHANGED` | Full address           |
| `CUSTOMER_DELETED`         | CustomerId             |

---

### 3.2 Tool

**Package:** `de.schaffbar.core_pos.tool`

**Aggregate Root:** `Tool`

**Identity:** `ToolId` (UUID)

#### Attributes

| Field              | Type             | Constraints                                          |
|--------------------|------------------|------------------------------------------------------|
| `id`               | `UUID`           | PK                                                   |
| `name`             | `String`         | `@NotBlank`                                          |
| `description`      | `String`         | optional                                             |
| `rfidReaderId`     | `UUID`           | optional, FK-like reference                          |
| `wlanRelaisType`   | `WlanRelaisType` | optional enum (`SHELLY_1`, `SHELLY_2`, `SHELLY_PRO`) |
| `ipAddress`        | `String`         | required when `wlanRelaisType` is set                |
| `httpStartCommand` | `String`         | auto-derived from `WlanRelaisType` template          |
| `onCommand`        | `String`         | auto-derived from `WlanRelaisType` template          |
| `offCommand`       | `String`         | auto-derived from `WlanRelaisType` template          |
| `createdAt`        | `Instant`        | `@NotNull`                                           |
| `updatedAt`        | `Instant`        | `@Version`                                           |

#### Commands

| Command                   | Fields                                                                      |
|---------------------------|-----------------------------------------------------------------------------|
| `CreateToolCommand`       | name, description, rfidReaderId (`RfidReaderId`), wlanRelaisType, ipAddress |
| `UpdateToolCommand`       | name, description                                                           |
| `UpdateWlanRelaisCommand` | wlanRelaisType, ipAddress                                                   |

#### Business Rules / Invariants

| #   | Rule                                                                                                                                    | Enforced in                                                      |
|-----|-----------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------|
| T-1 | `name` must not be blank                                                                                                                | Bean Validation                                                  |
| T-2 | Tool name must be unique across all tools                                                                                               | `ToolService.createTool()`                                       |
| T-3 | An RFID reader can only be assigned to one tool at a time                                                                               | `ToolService.createTool()`, `ToolService.assignRfidReader()`     |
| T-4 | An IP address can only be used by one tool at a time                                                                                    | `ToolService.createTool()`, `ToolService.updateWlanRelais()`     |
| T-5 | When `wlanRelaisType` is set, `ipAddress` is required                                                                                   | `Tool.applyWlanRelaisType()` — throws `IllegalArgumentException` |
| T-6 | WLAN-Relais commands (`httpStartCommand`, `onCommand`, `offCommand`) are derived from the `WlanRelaisType` template — not user-settable | `Tool.applyWlanRelaisType()`                                     |
| T-7 | Clearing WLAN-Relais resets all relais-related fields to null                                                                           | `Tool.clearWlanRelais()`                                         |
| T-8 | If assigning an RFID reader that is already assigned to the same tool → no-op (idempotent)                                              | `ToolService.assignRfidReader()`                                 |
| T-9 | Only RFID readers of type `SWITCH_BOX` can be assigned to tools                                                                         | `ToolCreate` / `ToolAssignRfidReader` use cases                  |

#### Domain Events

| Event                       | Payload                   |
|-----------------------------|---------------------------|
| `TOOL_CREATED`              | Full tool snapshot        |
| `TOOL_UPDATED`              | Updated name, description |
| `TOOL_WLAN_RELAIS_UPDATED`  | WLAN-Relais config        |
| `TOOL_RFID_READER_ASSIGNED` | toolId, rfidReaderId      |
| `TOOL_RFID_READER_CLEARED`  | toolId                    |
| `TOOL_DELETED`              | toolId                    |

---

### 3.3 RfidReader

**Package:** `de.schaffbar.core_pos.rfid_reader`

**Aggregate Root:** `RfidReader`

**Identity:** `RfidReaderId` (UUID)

#### Attributes

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
| Value | Code | Purpose |
|---|---|---|
| `RFID_TAG_REGISTER` | `A` | Registers new RFID tags |
| `RFID_TAG_ASSIGNER` | `C` | Assigns RFID tags to customers |
| `GATE_KEEPER_IN` | `GI` | Workshop entry gate |
| `GATE_KEEPER_OUT` | `GO` | Workshop exit gate |
| `SWITCH_BOX` | `S` | Controls tool power (paired with Tool) |

#### Commands

| Command                     | Fields                                                         |
|-----------------------------|----------------------------------------------------------------|
| _(Creation via MacAddress)_ | macAddress (`MacAddress` value object)                         |
| `UpdateRfidReaderCommand`   | id (`RfidReaderId`), type (`RfidReaderType`), name, socketName |

#### Business Rules / Invariants

| #   | Rule                                                | Enforced in                                                              |
|-----|-----------------------------------------------------|--------------------------------------------------------------------------|
| R-1 | `macAddress` must be unique across all RFID readers | DB unique constraint, **TODO** in `RfidReaderService.createRfidReader()` |
| R-2 | `macAddress` must not be blank                      | Bean Validation                                                          |

#### Domain Events

| Event                 | Payload              |
|-----------------------|----------------------|
| `RFID_READER_CREATED` | Full reader snapshot |
| `RFID_READER_UPDATED` | Updated fields       |
| `RFID_READER_DELETED` | rfidReaderId         |

---

### 3.4 RfidTag

**Package:** `de.schaffbar.core_pos.rfid_tag`

**Aggregate Root:** `RfidTag`

**Identity:** `RfidTagId` (String — hardware ID)

#### Attributes

| Field       | Type      | Constraints                    |
|-------------|-----------|--------------------------------|
| `id`        | `String`  | PK (hardware tag ID)           |
| `active`    | `boolean` | defaults to `true` on creation |
| `createdAt` | `Instant` | `@NotNull`                     |
| `updatedAt` | `Instant` | `@Version`                     |

#### Commands

| Command                | Fields             |
|------------------------|--------------------|
| `CreateRfidTagCommand` | rfidTagId (String) |

#### Business Rules / Invariants

| #    | Rule                                 | Enforced in                                     |
|------|--------------------------------------|-------------------------------------------------|
| RT-1 | RFID Tag ID must not be blank        | `@NotBlank` on `CreateRfidTagCommand.rfidTagId` |
| RT-2 | Newly created tags are always active | `RfidTag.of()` sets `active = true`             |

#### Domain Events

| Event              | Payload           |
|--------------------|-------------------|
| `RFID_TAG_CREATED` | Full tag snapshot |
| `RFID_TAG_DELETED` | rfidTagId         |

---

### 3.5 RfidTagAssignment

**Package:** `de.schaffbar.core_pos.rfid_tag_assignment`

**Aggregate Root:** `RfidTagAssignment`

**Identity:** `RfidTagAssignmentId` (UUID)

#### Attributes

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

#### Commands

| Operation          | Input                      |
|--------------------|----------------------------|
| Request assignment | customerId, assignmentType |
| Assign RFID tag    | rfidTagId                  |
| Unassign           | customerId                 |

#### Business Rules / Invariants

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

#### Domain Events

| Event                           | Payload             |
|---------------------------------|---------------------|
| `RFID_TAG_ASSIGNMENT_REQUESTED` | Assignment snapshot |
| `RFID_TAG_ASSIGNED`             | Assignment snapshot |
| `RFID_TAG_UNASSIGNED`           | Assignment snapshot |

---

### 3.6 RfidTagAssignmentHistory

**Package:** `de.schaffbar.core_pos.rfid_tag_assignment_history`

**Aggregate Root:** `RfidTagAssignmentHistory`

**Identity:** `RfidTagAssignmentId` (UUID) — _Note: FIXME in code suggests renaming to `RfidTagAssignmentHistoryId`_

#### Attributes

| Field              | Type                    | Constraints                  |
|--------------------|-------------------------|------------------------------|
| `id`               | `UUID`                  | PK                           |
| `customerId`       | `UUID`                  | `@NotNull`                   |
| `rfidTagId`        | `String`                | `@NotNull`                   |
| `assignmentType`   | `RfidTagAssignmentType` | `@NotNull`                   |
| `assignmentDate`   | `Instant`               | `@NotNull`, `@Past`          |
| `unassignmentDate` | `Instant`               | `@NotNull`, `@PastOrPresent` |
| `updatedAt`        | `Instant`               | `@Version`                   |

#### Business Rules / Invariants

| #     | Rule                                                                     | Enforced in                                          |
|-------|--------------------------------------------------------------------------|------------------------------------------------------|
| TAH-1 | History is created from a previously active (ASSIGNED) RfidTagAssignment | `RfidTagAssignmentHistory.of(RfidTagAssignmentView)` |
| TAH-2 | `assignmentDate` must be in the past                                     | `@Past` validation                                   |
| TAH-3 | `unassignmentDate` must be in the past or present                        | `@PastOrPresent` validation                          |
| TAH-4 | History records are immutable — no update commands                       | No command methods                                   |

#### Domain Events

_None — this is a projection / audit log aggregate._

---

### 3.7 WorkshopSession

**Package:** `de.schaffbar.core_pos.workshop_session`

**Aggregate Root:** `WorkshopSession`

**Identity:** `WorkshopSessionId` (UUID)

#### Attributes

| Field        | Type                    | Constraints                   |
|--------------|-------------------------|-------------------------------|
| `id`         | `UUID`                  | PK                            |
| `customerId` | `UUID`                  | `@NotNull`                    |
| `startTime`  | `Instant`               | `@NotNull`                    |
| `closeTime`  | `Instant`               | optional, set on close        |
| `status`     | `WorkshopSessionStatus` | `@NotNull` (`OPEN` or `PAID`) |
| `updatedAt`  | `Instant`               | `@Version`                    |

**Lifecycle:** `OPEN` → `PAID`

#### Commands

| Operation     | Input      |
|---------------|------------|
| Start session | customerId |
| Close session | customerId |

#### Business Rules / Invariants

| #    | Rule                                                     | Enforced in                                                                |
|------|----------------------------------------------------------|----------------------------------------------------------------------------|
| WS-1 | A customer can have at most one `OPEN` session at a time | `WorkshopSessionService.startSession()` — checks for existing open session |
| WS-2 | Closing sets status to `PAID` and records `closeTime`    | `WorkshopSession.close()`                                                  |
| WS-3 | Customer must exist before closing a session             | `CloseSession` use case                                                    |
| WS-4 | Cannot close a session that doesn't exist                | `WorkshopSessionService.closeSession()`                                    |
| WS-5 | Pending workshop usages should be checked before closing | **TODO** — noted in `CloseSession` use case                                |

#### Domain Events

| Event                      | Payload          |
|----------------------------|------------------|
| `WORKSHOP_SESSION_STARTED` | Session snapshot |
| `WORKSHOP_SESSION_CLOSED`  | Session snapshot |

---

### 3.8 WorkshopUsage

**Package:** `de.schaffbar.core_pos.workshop_usage`

**Aggregate Root:** `WorkshopUsage`

**Identity:** `WorkshopUsageId` (UUID)

_Note: Code suggests considering renaming to `WorkshopSlot` or `UsageSlot`._

#### Attributes

| Field               | Type      | Constraints           |
|---------------------|-----------|-----------------------|
| `id`                | `UUID`    | PK                    |
| `customerId`        | `UUID`    | `@NotNull`            |
| `workshopSessionId` | `UUID`    | `@NotNull`            |
| `entryTime`         | `Instant` | `@NotNull`            |
| `exitTime`          | `Instant` | optional, set on exit |
| `updatedAt`         | `Instant` | `@Version`            |

#### Commands

| Operation      | Input                         |
|----------------|-------------------------------|
| Enter workshop | customerId, workshopSessionId |
| Leave workshop | customerId, workshopSessionId |

#### Business Rules / Invariants

| #    | Rule                                                                            | Enforced in                                                                                   |
|------|---------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| WU-1 | A customer can have at most one active (no `exitTime`) workshop usage at a time | `WorkshopUsageService.enterWorkshop()` — checks for open usage via `findActiveByCustomerId()` |
| WU-2 | Customer must have an active usage to leave                                     | `WorkshopUsageService.leaveWorkshop()` — throws `NoActiveWorkshopUsageFoundException`         |
| WU-3 | Duration is computed as `exitTime - entryTime` (truncated to seconds)           | `WorkshopUsage.getDuration()` — returns `null` if either time is missing                      |
| WU-4 | A workshop session must exist (OPEN) before entering                            | `EnterWorkshop` use case — creates session if not present                                     |
| WU-5 | Active tool usages are stopped when leaving the workshop                        | `LeaveWorkshop` use case — calls `ToolUsageService.stopAllUsagesForCustomer()`                |
| WU-6 | Customer must have a valid certificate to enter                                 | **TODO** — noted in `EnterWorkshop` use case                                                  |

#### Domain Events

| Event                    | Payload        |
|--------------------------|----------------|
| `WORKSHOP_USAGE_ENTERED` | Usage snapshot |
| `WORKSHOP_USAGE_LEFT`    | Usage snapshot |

---

### 3.9 ToolUsage

**Package:** `de.schaffbar.core_pos.tool_usage`

**Aggregate Root:** `ToolUsage`

**Identity:** `ToolUsageId` (UUID)

#### Attributes

| Field               | Type      | Constraints                |
|---------------------|-----------|----------------------------|
| `id`                | `UUID`    | PK                         |
| `customerId`        | `UUID`    | `@NotNull`                 |
| `toolId`            | `UUID`    | `@NotNull`                 |
| `workshopSessionId` | `UUID`    | `@NotNull`                 |
| `startTime`         | `Instant` | `@NotNull`                 |
| `endTime`           | `Instant` | optional, set when stopped |
| `updatedAt`         | `Instant` | `@Version`                 |

#### Commands

| Command                 | Fields                                |
|-------------------------|---------------------------------------|
| `StartToolUsageCommand` | customerId, toolId, workshopSessionId |

#### Business Rules / Invariants

| #    | Rule                                                                                   | Enforced in                                                                                             |
|------|----------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------|
| TU-1 | A customer can use at most 2 tools simultaneously                                      | `ToolUsageService.startUsage()` — checks count via `countActiveByCustomerId()`                          |
| TU-2 | A customer can only use tools when actively in the workshop (has active WorkshopUsage) | `StartToolUsage` use case — calls `verifyCustomerIsInWorkshop()`                                        |
| TU-3 | A customer cannot start the same tool twice simultaneously                             | `ToolUsageService.startUsage()` — checks via `findActiveByCustomerIdAndToolId()`                        |
| TU-4 | When a customer leaves the workshop, all active tool usages are automatically stopped  | `LeaveWorkshop` use case — calls `ToolUsageService.stopAllUsagesForCustomer()`                          |
| TU-5 | Tool on/off detection happens via RFID reader (type SWITCH_BOX) assigned to the tool   | `DeviceController` — resolves tool via `ToolService.getTool(RfidReaderId)` and toggles start/stop       |
| TU-6 | Customer identification happens via RFID tag assignment                                | `DeviceController` — resolves customer via `RfidTagAssignmentService`                                   |
| TU-7 | Duration is computed as `endTime - startTime` (truncated to seconds)                   | `ToolUsage.getDuration()` — returns `null` if either time is missing                                    |
| TU-8 | Customer must have an open workshop session to start a tool usage                      | `StartToolUsage` use case — checks for active session                                                   |
| TU-9 | A tool can only be used by one customer at a time                                      | `ToolUsageService.startUsage()` — checks via `findActiveByToolId()`, throws `ToolAlreadyInUseException` |

#### Domain Events

| Event                | Payload             |
|----------------------|---------------------|
| `TOOL_USAGE_STARTED` | Tool usage snapshot |
| `TOOL_USAGE_STOPPED` | Tool usage snapshot |

---

## 4. Use Cases (Application Services)

Use cases live in the `use_case` package and **orchestrate operations across multiple aggregates**. They are the only
place where cross-aggregate coordination happens.

| Use Case                           | Description                                                                                 | Aggregates Involved                                   |
|------------------------------------|---------------------------------------------------------------------------------------------|-------------------------------------------------------|
| `EnterWorkshop`                    | Customer enters the workshop. Creates a session if none exists, then creates a usage entry. | WorkshopSession, WorkshopUsage                        |
| `LeaveWorkshop`                    | Customer leaves the workshop. Stops all active tool usages, then records exit time.         | WorkshopSession, WorkshopUsage, ToolUsage             |
| `CloseSession`                     | Closes a customer's workshop session (mark as PAID).                                        | Customer, WorkshopSession                             |
| `StartToolUsage`                   | Starts tool usage for a customer. Verifies workshop presence and open session.              | ToolUsage, WorkshopSession, WorkshopUsage             |
| `StopToolUsage`                    | Stops tool usage for a customer on a specific tool.                                         | ToolUsage                                             |
| `CustomerRequestRfidTagAssignment` | Initiates RFID tag assignment process for a customer.                                       | Customer, RfidTagAssignment                           |
| `CustomerAssignRfidTag`            | Completes RFID tag assignment by linking tag to pending assignment.                         | RfidTag, RfidTagAssignment                            |
| `CustomerUnassignRfidTag`          | Removes RFID tag from customer, moves record to history.                                    | Customer, RfidTagAssignment, RfidTagAssignmentHistory |
| `ToolCreate`                       | Creates a tool with optional RFID reader validation (must be SWITCH_BOX).                   | RfidReader, Tool                                      |
| `ToolAssignRfidReader`             | Assigns an RFID reader to a tool (reader must be SWITCH_BOX).                               | RfidReader, Tool                                      |

---

## 5. Read Models (Query Services)

| Service                    | Endpoint concept      | Description                                                                                                                   |
|----------------------------|-----------------------|-------------------------------------------------------------------------------------------------------------------------------|
| `WorkshopDashboardService` | Active workshop users | Cross-aggregate query combining active `WorkshopUsage` entries with `Customer` data to show who is currently in the workshop. |

The `WorkshopDashboardEntryView` provides:

- `customerId`, `firstName`, `lastName`, `entryTime`

---

## 6. Value Objects

All value objects are located in `de.schaffbar.core_pos.shared.id`:

| Value Object          | Wraps    | Used for                        |
|-----------------------|----------|---------------------------------|
| `CustomerId`          | `UUID`   | Customer identity               |
| `ToolId`              | `UUID`   | Tool identity                   |
| `ToolUsageId`         | `UUID`   | Tool Usage identity             |
| `RfidReaderId`        | `UUID`   | RFID Reader identity            |
| `RfidTagId`           | `String` | RFID Tag identity (hardware ID) |
| `RfidTagAssignmentId` | `UUID`   | RFID Tag Assignment identity    |
| `WorkshopSessionId`   | `UUID`   | Workshop Session identity       |
| `WorkshopUsageId`     | `UUID`   | Workshop Usage identity         |
| `MacAddress`          | `String` | RFID Reader MAC address         |

---

## 7. Domain Events

### Event Infrastructure

- **`SchaffbarEvent`** — envelope containing `id`, `type`, `version`, `aggregateType`, `aggregateId`, `timestamp`,
  `payload` (JSON), `owner`
- **`OutboxEvent`** — JPA entity persisted to `schaffbar.outbox_event` table within the same transaction
- **`EventPayload`** — marker interface with validation support
- **`AggregateType`** — enum identifying the source aggregate

### Event Flow

```
Command Method (Aggregate Root)
  └── Returns List<SchaffbarEvent>
        └── Service saves to OutboxEventRepository
              └── outbox_event table (transactional outbox pattern)
                    └── Polled/published by external process
```

### Event Creation Pattern

```
EventFactory.someEvent(aggregate)
  ├── PayloadMapper.MAPPER.toSomePayload(aggregate)   // MapStruct mapping
  ├── payload.validate()                                // Bean Validation
  └── SchaffbarEvent.<aggregateType>Event(type, id, payload)  // Envelope creation
```

### Complete Event List

| Aggregate         | Event Type                                                                                                                          |
|-------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| Customer          | `CUSTOMER_CREATED`, `CUSTOMER_UPDATED`, `CUSTOMER_CONTACT_CHANGED`, `CUSTOMER_ADDRESS_CHANGED`, `CUSTOMER_DELETED`                  |
| Tool              | `TOOL_CREATED`, `TOOL_UPDATED`, `TOOL_WLAN_RELAIS_UPDATED`, `TOOL_RFID_READER_ASSIGNED`, `TOOL_RFID_READER_CLEARED`, `TOOL_DELETED` |
| ToolUsage         | `TOOL_USAGE_STARTED`, `TOOL_USAGE_STOPPED`                                                                                          |
| RfidReader        | `RFID_READER_CREATED`, `RFID_READER_UPDATED`, `RFID_READER_DELETED`                                                                 |
| RfidTag           | `RFID_TAG_CREATED`, `RFID_TAG_DELETED`                                                                                              |
| RfidTagAssignment | `RFID_TAG_ASSIGNMENT_REQUESTED`, `RFID_TAG_ASSIGNED`, `RFID_TAG_UNASSIGNED`                                                         |
| WorkshopSession   | `WORKSHOP_SESSION_STARTED`, `WORKSHOP_SESSION_CLOSED`                                                                               |
| WorkshopUsage     | `WORKSHOP_USAGE_ENTERED`, `WORKSHOP_USAGE_LEFT`                                                                                     |

---

## Appendix: Known TODOs and Open Design Questions

| Location                               | Description                                                                          |
|----------------------------------------|--------------------------------------------------------------------------------------|
| `CustomerService.deleteCustomer()`     | Deletion concept needed — check RFID tag assignment before deleting                  |
| `RfidReaderService.createRfidReader()` | Check for duplicate MAC address on creation                                          |
| `RfidTagAssignment.assignRfidTag()`    | Validate that assignment is in `WAITING_FOR_ASSIGNMENT` state                        |
| `EnterWorkshop.process()`              | Check if customer has a valid certificate to enter                                   |
| `CloseSession.process()`               | Check for pending workshop usages before closing                                     |
| `WorkshopUsage`                        | Consider renaming to `WorkshopSlot` or `UsageSlot`                                   |
| `RfidTagAssignmentHistory`             | Consider renaming ID type to `RfidTagAssignmentHistoryId`                            |
| `RfidTagCommands.CreateRfidTagCommand` | Switch `rfidTagId` to `RfidTagId` value object                                       |
| `*Service.saveOutboxEvents()`          | DRY — method is duplicated in all services, extract to base class or utility         |
| `WorkshopSessionService`               | Naming inconsistency — use either "active" or "open" session consistently            |
| `EventType`                            | Consider renaming `WORKSHOP_USAGE_ENTERED` / `LEFT` to `WORKSHOP_ENTERED` / `EXITED` |
