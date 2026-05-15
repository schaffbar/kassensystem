# ToolCertification

**Aggregate Root:** `ToolCertification`

**Identity:** `ToolCertificationId` (UUID)

## Attributes

| Field         | Type                       | Constraints |
|---------------|----------------------------|-------------|
| `id`          | `UUID`                     | PK          |
| `customerId`  | `UUID`                     | `@NotNull`  |
| `toolId`      | `UUID`                     | `@NotNull`  |
| `certifiedBy` | `UUID`                     | optional    |
| `status`      | `ToolCertificationStatus`  | `@NotNull`  |
| `certifiedAt` | `Instant`                  | `@NotNull`  |
| `updatedAt`   | `Instant`                  | `@Version`  |

**Status Enum:** `ToolCertificationStatus` — `ACTIVE`, `PAUSED`, `REVOKED`

**Lifecycle:** `ACTIVE` ↔ `PAUSED`, `ACTIVE`/`PAUSED` → `REVOKED` (terminal state)

**Note:** `of(CreateToolCertificationCommand)` returns `ToolCertification` directly (no `WithEvents` record). The creation event is produced in the Service after persisting. Command methods (`pause()`, `reactivate()`, `revoke()`) return `List<SchaffbarEvent>`.

## Commands

| Command                          | Fields                          |
|----------------------------------|---------------------------------|
| `CreateToolCertificationCommand` | customerId, toolId, certifiedBy |

## Business Rules / Invariants

| #    | Rule                                                                                                 | Enforced in                                                                                                     |
|------|------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| TC-1 | A customer can have at most one certification per tool (unique constraint on `customer_id, tool_id`) | DB unique constraint + `ToolCertificationService.create()`                                                      |
| TC-2 | REVOKED is a terminal state — the customer can never be re-certified for that tool                   | `ToolCertificationService.create()` — throws `CertificationPermanentlyRevokedException`                        |
| TC-3 | Only valid state transitions: ACTIVE→PAUSED, PAUSED→ACTIVE, ACTIVE/PAUSED→REVOKED                   | `ToolCertification.pause()`, `reactivate()`, `revoke()` — throw `InvalidCertificationStateTransitionException` |
| TC-4 | Batch certification supports partial success with error report                                       | `CertifyCustomersForTool` use case — catches exceptions per customer, returns `BatchCertificationResult`        |

## Domain Events

| Event                            | Payload                     |
|----------------------------------|-----------------------------|
| `TOOL_CERTIFICATION_CREATED`     | Full certification snapshot |
| `TOOL_CERTIFICATION_PAUSED`      | Certification id + status   |
| `TOOL_CERTIFICATION_REACTIVATED` | Certification id + status   |
| `TOOL_CERTIFICATION_REVOKED`     | Certification id + status   |
