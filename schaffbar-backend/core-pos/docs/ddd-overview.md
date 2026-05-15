# Schaffbar — Documentation

## Table of Contents

1. [Domain Overview](#1-domain-overview)
2. [Aggregate Structure Pattern](#2-aggregate-structure-pattern)
3. [Aggregate Catalog](#3-aggregate-catalog)
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
- **Tool certification management** — per-customer, per-tool certifications (Einweisungen) that gate access to machines (ACTIVE/PAUSED/REVOKED lifecycle)
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

Each aggregate's detailed documentation (attributes, commands, business rules, domain events) lives in a `README.md`
inside its package. This keeps the documentation close to the code it describes.

| #    | Aggregate                  | Package                                            | Identity                 | README                                                                          |
|------|----------------------------|----------------------------------------------------|--------------------------|---------------------------------------------------------------------------------|
| 3.1  | Customer                   | `de.schaffbar.core_pos.customer`                   | `CustomerId` (UUID)      | [customer/README.md](../src/main/java/de/schaffbar/core_pos/customer/README.md) |
| 3.2  | Tool                       | `de.schaffbar.core_pos.tool`                       | `ToolId` (UUID)          | [tool/README.md](../src/main/java/de/schaffbar/core_pos/tool/README.md)         |
| 3.3  | RfidReader                 | `de.schaffbar.core_pos.rfid_reader`                | `RfidReaderId` (UUID)    | [rfid_reader/README.md](../src/main/java/de/schaffbar/core_pos/rfid_reader/README.md) |
| 3.4  | RfidTag                    | `de.schaffbar.core_pos.rfid_tag`                   | `RfidTagId` (String)     | [rfid_tag/README.md](../src/main/java/de/schaffbar/core_pos/rfid_tag/README.md) |
| 3.5  | RfidTagAssignment          | `de.schaffbar.core_pos.rfid_tag_assignment`        | `RfidTagAssignmentId` (UUID) | [rfid_tag_assignment/README.md](../src/main/java/de/schaffbar/core_pos/rfid_tag_assignment/README.md) |
| 3.6  | RfidTagAssignmentHistory   | `de.schaffbar.core_pos.rfid_tag_assignment_history` | `RfidTagAssignmentId` (UUID) | [rfid_tag_assignment_history/README.md](../src/main/java/de/schaffbar/core_pos/rfid_tag_assignment_history/README.md) |
| 3.7  | WorkshopSession            | `de.schaffbar.core_pos.workshop_session`           | `WorkshopSessionId` (UUID) | [workshop_session/README.md](../src/main/java/de/schaffbar/core_pos/workshop_session/README.md) |
| 3.8  | WorkshopUsage              | `de.schaffbar.core_pos.workshop_usage`             | `WorkshopUsageId` (UUID) | [workshop_usage/README.md](../src/main/java/de/schaffbar/core_pos/workshop_usage/README.md) |
| 3.9  | ToolUsage                  | `de.schaffbar.core_pos.tool_usage`                 | `ToolUsageId` (UUID)     | [tool_usage/README.md](../src/main/java/de/schaffbar/core_pos/tool_usage/README.md) |
| 3.10 | ToolCertification          | `de.schaffbar.core_pos.tool_certification`         | `ToolCertificationId` (UUID) | [tool_certification/README.md](../src/main/java/de/schaffbar/core_pos/tool_certification/README.md) |

---

## 4. Use Cases (Application Services)

Use cases live in the `use_case` package and **orchestrate operations across multiple aggregates**. They are the only
place where cross-aggregate coordination happens.

| Use Case                           | Description                                                                                       | Aggregates Involved                                   |
|------------------------------------|---------------------------------------------------------------------------------------------------|-------------------------------------------------------|
| `EnterWorkshop`                    | Customer enters the workshop. Creates a session if none exists, then creates a usage entry.       | WorkshopSession, WorkshopUsage                        |
| `LeaveWorkshop`                    | Customer leaves the workshop. Stops all active tool usages, then records exit time.               | WorkshopSession, WorkshopUsage, ToolUsage             |
| `CloseSession`                     | Closes a customer's workshop session (mark as PAID).                                              | Customer, WorkshopSession                             |
| `StartToolUsage`                   | Starts tool usage for a customer. Verifies tool certification, workshop presence, and open session. | ToolUsage, WorkshopSession, WorkshopUsage, ToolCertification |
| `StopToolUsage`                    | Stops tool usage for a customer on a specific tool.                                               | ToolUsage                                             |
| `CertifyCustomersForTool`          | Creates certifications for multiple customers after a training session. Supports partial success with error report. | ToolCertification                                     |
| `PauseToolCertification`           | Pauses a customer's tool certification.                                                           | ToolCertification                                     |
| `ReactivateToolCertification`      | Reactivates a paused tool certification.                                                          | ToolCertification                                     |
| `RevokeToolCertification`          | Permanently revokes a customer's tool certification.                                              | ToolCertification                                     |
| `CustomerRequestRfidTagAssignment` | Initiates RFID tag assignment process for a customer.                                             | Customer, RfidTagAssignment                           |
| `CustomerAssignRfidTag`            | Completes RFID tag assignment by linking tag to pending assignment.                               | RfidTag, RfidTagAssignment                            |
| `CustomerUnassignRfidTag`          | Removes RFID tag from customer, moves record to history.                                          | Customer, RfidTagAssignment, RfidTagAssignmentHistory |
| `ToolCreate`                       | Creates a tool with optional RFID reader validation (must be SWITCH_BOX).                         | RfidReader, Tool                                      |
| `ToolAssignRfidReader`             | Assigns an RFID reader to a tool (reader must be SWITCH_BOX).                                     | RfidReader, Tool                                      |
| `ChangeRfidReaderType`             | Changes the type of an RFID reader. If current type is SWITCH_BOX and a tool is assigned → error. | RfidReader, Tool                                      |

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
| `ToolCertificationId` | `UUID`   | Tool Certification identity     |
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
| ToolCertification | `TOOL_CERTIFICATION_CREATED`, `TOOL_CERTIFICATION_PAUSED`, `TOOL_CERTIFICATION_REACTIVATED`, `TOOL_CERTIFICATION_REVOKED`             |
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
| `EnterWorkshop.process()`              | ~~Check if customer has a valid certificate to enter~~ — resolved via `StartToolUsage` certification check |
| `CloseSession.process()`               | Check for pending workshop usages before closing                                     |
| `WorkshopUsage`                        | Consider renaming to `WorkshopSlot` or `UsageSlot`                                   |
| `RfidTagAssignmentHistory`             | Consider renaming ID type to `RfidTagAssignmentHistoryId`                            |
| `RfidTagCommands.CreateRfidTagCommand` | Switch `rfidTagId` to `RfidTagId` value object                                       |
| `*Service.saveOutboxEvents()`          | DRY — method is duplicated in all services, extract to base class or utility         |
| `WorkshopSessionService`               | Naming inconsistency — use either "active" or "open" session consistently            |
| `EventType`                            | Consider renaming `WORKSHOP_USAGE_ENTERED` / `LEFT` to `WORKSHOP_ENTERED` / `EXITED` |
