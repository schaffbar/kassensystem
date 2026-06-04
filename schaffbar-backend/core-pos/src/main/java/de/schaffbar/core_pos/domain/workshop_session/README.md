# WorkshopSession

**Aggregate Root:** `WorkshopSession`

**Identity:** `WorkshopSessionId` (UUID)

## Attributes

| Field        | Type                    | Constraints                   |
|--------------|-------------------------|-------------------------------|
| `id`         | `UUID`                  | PK                            |
| `customerId` | `UUID`                  | `@NotNull`                    |
| `startTime`  | `Instant`               | `@NotNull`                    |
| `closeTime`  | `Instant`               | optional, set on close        |
| `status`     | `WorkshopSessionStatus` | `@NotNull` (`OPEN` or `PAID`) |
| `updatedAt`  | `Instant`               | `@Version`                    |

**Lifecycle:** `OPEN` → `PAID`

## Commands

| Operation     | Input      |
|---------------|------------|
| Start session | customerId |
| Close session | customerId |

## Business Rules / Invariants

| #    | Rule                                                     | Enforced in                                                                |
|------|----------------------------------------------------------|----------------------------------------------------------------------------|
| WS-1 | A customer can have at most one `OPEN` session at a time | `WorkshopSessionService.startSession()` — checks for existing open session |
| WS-2 | Closing sets status to `PAID` and records `closeTime`    | `WorkshopSession.close()`                                                  |
| WS-3 | Customer must exist before closing a session             | `CloseSession` use case                                                    |
| WS-4 | Cannot close a session that doesn't exist                | `WorkshopSessionService.closeSession()`                                    |
| WS-5 | Pending workshop usages should be checked before closing | **TODO** — noted in `CloseSession` use case                                |

## Domain Events

| Event                      | Payload          |
|----------------------------|------------------|
| `WORKSHOP_SESSION_STARTED` | Session snapshot |
| `WORKSHOP_SESSION_CLOSED`  | Session snapshot |
