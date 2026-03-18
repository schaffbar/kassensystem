# Schaffbar - Dokumentation

## Inhaltsverzeichnis

1. [Domänenübersicht](#1-domänenübersicht)
2. [Aggregat-Strukturmuster](#2-aggregat-strukturmuster)
3. [Aggregat-Katalog](#3-aggregat-katalog)
    - [Customer (Kunde)](#31-customer-kunde)
    - [Tool (Werkzeug)](#32-tool-werkzeug)
    - [RfidReader (RFID-Lesegerät)](#33-rfidreader-rfid-lesegerät)
    - [RfidTag (RFID-Chip)](#34-rfidtag-rfid-chip)
    - [RfidTagAssignment (RFID-Chip-Zuordnung)](#35-rfidtagassignment-rfid-chip-zuordnung)
    - [RfidTagAssignmentHistory (Zuordnungshistorie)](#36-rfidtagassignmenthistory-zuordnungshistorie)
    - [WorkshopSession (Werkstatt-Sitzung)](#37-workshopsession-werkstatt-sitzung)
    - [WorkshopUsage (Werkstatt-Nutzung)](#38-workshopusage-werkstatt-nutzung)
    - [ToolUsage (Werkzeugnutzung)](#39-toolusage-werkzeugnutzung)
4. [Anwendungsfälle (Application Services)](#4-anwendungsfälle-application-services)
5. [Lesemodelle (Query Services)](#5-lesemodelle-query-services)
6. [Wertobjekte (Value Objects)](#6-wertobjekte-value-objects)
7. [Domänenereignisse (Domain Events)](#7-domänenereignisse-domain-events)

---

## 1. Domänenübersicht

Das **Schaffbar Core-POS**-System verwaltet eine **Werkstatt**, in der Kunden eintreten, Werkzeuge nutzen und nach
Zeitaufwand abgerechnet werden können. Die Kerndomäne umfasst:

- **Kundenverwaltung** — Personendaten, Kontaktinformationen, Adressen
- **RFID-Infrastruktur** — Lesegeräte, Chips und deren Zuordnung zu Kunden
- **Werkzeugverwaltung** — Werkstattwerkzeuge mit optionaler WLAN-Relais-Steuerung (Shelly) und RFID-Lesegerät-Kopplung
- **Werkzeugnutzungsverfolgung** — erfasst welcher Kunde welches Werkzeug nutzt, für Abrechnung und Sicherheit
- **Werkstattzugangsverfolgung** — Sitzungen (Abrechnungseinheiten) und Nutzungen (einzelne Ein-/Austritts-Zeitslots)
- **Werkstatt-Dashboard** — ein Lesemodell, das aktuell aktive Nutzer anzeigt

### Bounded Context

Alle Aggregate befinden sich in einem einzigen Bounded Context: **Core-POS**. Die Koordination zwischen Aggregaten
erfolgt über **Anwendungsfälle** (Application-Level Services im Paket `use_case`), die Aufrufe über mehrere
Domänendienste hinweg orchestrieren.

### Ereignisgesteuerte Architektur

Jede Zustandsänderung erzeugt **`SchaffbarEvent`**-Instanzen, die in einer **transaktionalen Outbox** (`outbox_event`
-Tabelle) innerhalb derselben Transaktion persistiert werden. Dies ermöglicht eine zuverlässige Ereignisveröffentlichung
für nachgelagerte Konsumenten.

---

## 2. Aggregat-Strukturmuster

Jedes Aggregat in diesem Projekt folgt einer einheitlichen internen Struktur. Dieser Abschnitt dokumentiert das Muster,
damit jedes Modul leicht verständlich und erweiterbar ist.

```
<aggregat_name>/
│
├── <Aggregat>.java               # Aggregat-Root (Entität)
├── <Aggregat>Commands.java       # Command-Records (Eingabe-DTOs)
├── <Aggregat>Service.java        # Domänen-/Anwendungsdienst
├── <Aggregat>Repository.java     # Spring Data JPA Repository
├── <Aggregat>Views.java          # MapStruct-basierte schreibgeschützte Projektionen (View-Records)
├── <Aggregat>EventFactory.java   # Statische Fabrik zur Erstellung von SchaffbarEvents
├── <Aggregat>PayloadMapper.java  # MapStruct-Mapper: Entität → Event-Payload
├── <Status/Typ-Enums>.java       # Status- oder Typ-Enumerationen (falls vorhanden)
├── <Wertobjekte>.java            # Eingebettete Wertobjekte (falls vorhanden, z.B. CustomerAddress)
│
└── web/
    ├── <Aggregat>Controller.java    # REST-Controller
    └── <Aggregat>ApiModel.java      # API-Anfrage-/Antwortmodelle
```

### Komponentenverantwortlichkeiten

| Komponente                | Verantwortlichkeit                                                                                                                                                                                                                                                   |
|---------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Aggregat-Root**         | Kapselt Zustand und Invarianten. Alle Mutationen erfolgen über Command-Methoden, die `List<SchaffbarEvent>` zurückgeben. Statische `of(...)`-Fabrik für die Erstellung gibt ein `*WithEvents`-Record zurück, das die Entität und ihre Erstellungsereignisse enthält. |
| **Commands**              | Unveränderliche Java-`record`-Typen mit Bean-Validation-Annotationen. Gruppiert in einem `*Commands`-Interface.                                                                                                                                                      |
| **Service**               | Validiert Existenz, erzwingt Eindeutigkeitsconstraints (aggregatübergreifende Prüfungen), delegiert an das Aggregat-Root, persistiert Entität und Outbox-Events in einer einzigen `@Transactional`-Grenze.                                                           |
| **Repository**            | Spring Data `JpaRepository` mit benutzerdefinierten Abfragemethoden. Paket-privat.                                                                                                                                                                                   |
| **Views**                 | Schreibgeschützte `record`-Projektionen, gemappt via MapStruct. Stellt eine Singleton-`MAPPER`-Instanz bereit.                                                                                                                                                       |
| **EventFactory**          | Erstellt `SchaffbarEvent` für jeden Ereignistyp, indem die Entität über den `PayloadMapper` auf ein Payload gemappt, das Payload validiert und eingepackt wird.                                                                                                      |
| **PayloadMapper**         | MapStruct-Interface, das vom Aggregat-Root-Entität auf spezifische Event-Payload-Records mappt.                                                                                                                                                                      |
| **Controller / ApiModel** | REST-Schicht — mappt HTTP-Anfragen auf Commands und ruft Service oder Anwendungsfall auf.                                                                                                                                                                            |

### Interne Struktur des Aggregat-Roots

Jedes Aggregat-Root folgt demselben Code-Layout:

```java
class

<Aggregat> {

    // --- Felder (JPA @Id, Domänenattribute, @Version) ---

    // --- Statischer Konstruktor ---
    // static <WithEvents> of(<CreateCommand>) { ... }

    // --- Abfragen ---
    // getId() gibt typisiertes Wertobjekt zurück
    // weitere abgeleitete Abfragemethoden

    // --- Kommandos ---
    // List<SchaffbarEvent> update(...) { ... }
    // List<SchaffbarEvent> <domänenAktion>(...) { ... }

    // --- Hilfsmethoden ---
    // record <WithEvents>(<Aggregat>, List<SchaffbarEvent>) {}
}
```

**Wichtige Konventionen:**

- **Keine öffentlichen Setter** — Zustandsänderungen nur über benannte Command-Methoden
- **Jede Command-Methode gibt `List<SchaffbarEvent>` zurück** — der Aufrufer (Service) persistiert sie in die Outbox
- **Statische Fabrik `of(...)`** gibt ein `*WithEvents`-Record zurück (Entität + Ereignisse)
- **`@Version`-Feld** (`Instant updatedAt`) für optimistisches Locking
- **IDs werden intern als Rohtypen gespeichert** (`UUID`, `String`), aber über Getter-Methoden als **Wertobjekte**
  exponiert

---

## 3. Aggregat-Katalog

### 3.1 Customer (Kunde)

**Paket:** `de.schaffbar.core_pos.customer`

**Aggregat-Root:** `Customer`

**Identität:** `CustomerId` (UUID)

#### Attribute

| Feld          | Typ               | Constraints               |
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

**Wertobjekt:** `CustomerAddress` (eingebettet)

- `addressLine1` (`@NotBlank`), `addressLine2` (optional), `postalCode` (`@NotBlank`), `city` (`@NotBlank`), `country` (
  `@NotBlank`)

#### Kommandos

| Kommando                       | Felder                                                                                                            |
|--------------------------------|-------------------------------------------------------------------------------------------------------------------|
| `CreateCustomerCommand`        | firstName, lastName, dateOfBirth, clubMember, email, phone, addressLine1, addressLine2, postalCode, city, country |
| `UpdateCustomerCommand`        | id (`CustomerId`), firstName, lastName, dateOfBirth, clubMember                                                   |
| `UpdateCustomerContactCommand` | id (`CustomerId`), email, phone                                                                                   |
| `UpdateCustomerAddressCommand` | id (`CustomerId`), addressLine1, addressLine2, postalCode, city, country                                          |

#### Geschäftsregeln / Invarianten

| #   | Regel                                                                    | Durchgesetzt in                                           |
|-----|--------------------------------------------------------------------------|-----------------------------------------------------------|
| C-1 | `firstName`, `lastName`, `email` dürfen nicht leer sein                  | Bean Validation auf Entität + Commands                    |
| C-2 | `dateOfBirth` darf nicht null sein                                       | Bean Validation                                           |
| C-3 | Adresse muss immer vorhanden und gültig sein                             | `@NotNull` auf eingebettetem `CustomerAddress`            |
| C-4 | Kunde darf nicht gelöscht werden, wenn er einem RFID-Chip zugeordnet ist | **TODO** — vermerkt in `CustomerService.deleteCustomer()` |

#### Domänenereignisse

| Ereignis                   | Payload                       |
|----------------------------|-------------------------------|
| `CUSTOMER_CREATED`         | Vollständiger Kunden-Snapshot |
| `CUSTOMER_UPDATED`         | Aktualisierte Basisfelder     |
| `CUSTOMER_CONTACT_CHANGED` | E-Mail, Telefon               |
| `CUSTOMER_ADDRESS_CHANGED` | Vollständige Adresse          |
| `CUSTOMER_DELETED`         | CustomerId                    |

---

### 3.2 Tool (Werkzeug)

**Paket:** `de.schaffbar.core_pos.tool`

**Aggregat-Root:** `Tool`

**Identität:** `ToolId` (UUID)

#### Attribute

| Feld               | Typ              | Constraints                                                  |
|--------------------|------------------|--------------------------------------------------------------|
| `id`               | `UUID`           | PK                                                           |
| `name`             | `String`         | `@NotBlank`                                                  |
| `description`      | `String`         | optional                                                     |
| `rfidReaderId`     | `UUID`           | optional, FK-ähnliche Referenz                               |
| `wlanRelaisType`   | `WlanRelaisType` | optionale Enumeration (`SHELLY_1`, `SHELLY_2`, `SHELLY_PRO`) |
| `ipAddress`        | `String`         | erforderlich wenn `wlanRelaisType` gesetzt                   |
| `httpStartCommand` | `String`         | automatisch aus `WlanRelaisType`-Vorlage abgeleitet          |
| `onCommand`        | `String`         | automatisch aus `WlanRelaisType`-Vorlage abgeleitet          |
| `offCommand`       | `String`         | automatisch aus `WlanRelaisType`-Vorlage abgeleitet          |
| `createdAt`        | `Instant`        | `@NotNull`                                                   |
| `updatedAt`        | `Instant`        | `@Version`                                                   |

#### Kommandos

| Kommando                  | Felder                                                                      |
|---------------------------|-----------------------------------------------------------------------------|
| `CreateToolCommand`       | name, description, rfidReaderId (`RfidReaderId`), wlanRelaisType, ipAddress |
| `UpdateToolCommand`       | name, description                                                           |
| `UpdateWlanRelaisCommand` | wlanRelaisType, ipAddress                                                   |

#### Geschäftsregeln / Invarianten

| #   | Regel                                                                                                                                               | Durchgesetzt in                                                 |
|-----|-----------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------|
| T-1 | `name` darf nicht leer sein                                                                                                                         | Bean Validation                                                 |
| T-2 | Werkzeugname muss über alle Werkzeuge eindeutig sein                                                                                                | `ToolService.createTool()`                                      |
| T-3 | Ein RFID-Lesegerät kann gleichzeitig nur einem Werkzeug zugeordnet sein                                                                             | `ToolService.createTool()`, `ToolService.assignRfidReader()`    |
| T-4 | Eine IP-Adresse kann gleichzeitig nur von einem Werkzeug verwendet werden                                                                           | `ToolService.createTool()`, `ToolService.updateWlanRelais()`    |
| T-5 | Wenn `wlanRelaisType` gesetzt ist, ist `ipAddress` erforderlich                                                                                     | `Tool.applyWlanRelaisType()` — wirft `IllegalArgumentException` |
| T-6 | WLAN-Relais-Befehle (`httpStartCommand`, `onCommand`, `offCommand`) werden aus der `WlanRelaisType`-Vorlage abgeleitet — nicht vom Benutzer setzbar | `Tool.applyWlanRelaisType()`                                    |
| T-7 | Das Löschen des WLAN-Relais setzt alle Relais-bezogenen Felder auf null zurück                                                                      | `Tool.clearWlanRelais()`                                        |
| T-8 | Wenn ein bereits zugeordnetes RFID-Lesegerät demselben Werkzeug zugeordnet wird → keine Aktion (idempotent)                                         | `ToolService.assignRfidReader()`                                |
| T-9 | Nur RFID-Lesegeräte vom Typ `SWITCH_BOX` können Werkzeugen zugeordnet werden                                                                        | `ToolCreate` / `ToolAssignRfidReader` Anwendungsfälle           |

#### Domänenereignisse

| Ereignis                    | Payload                           |
|-----------------------------|-----------------------------------|
| `TOOL_CREATED`              | Vollständiger Werkzeug-Snapshot   |
| `TOOL_UPDATED`              | Aktualisierter Name, Beschreibung |
| `TOOL_WLAN_RELAIS_UPDATED`  | WLAN-Relais-Konfiguration         |
| `TOOL_RFID_READER_ASSIGNED` | toolId, rfidReaderId              |
| `TOOL_RFID_READER_CLEARED`  | toolId                            |
| `TOOL_DELETED`              | toolId                            |

---

### 3.3 RfidReader (RFID-Lesegerät)

**Paket:** `de.schaffbar.core_pos.rfid_reader`

**Aggregat-Root:** `RfidReader`

**Identität:** `RfidReaderId` (UUID)

#### Attribute

| Feld         | Typ              | Constraints                           |
|--------------|------------------|---------------------------------------|
| `id`         | `UUID`           | PK                                    |
| `macAddress` | `String`         | `@NotBlank`, `@Column(unique = true)` |
| `type`       | `RfidReaderType` | optionale Enumeration                 |
| `name`       | `String`         | optional                              |
| `socketName` | `String`         | optional                              |
| `createdAt`  | `Instant`        | `@NotNull`                            |
| `updatedAt`  | `Instant`        | `@Version`                            |

**Enumeration:** `RfidReaderType`
| Wert | Code | Zweck |
|---|---|---|
| `RFID_TAG_REGISTER` | `A` | Registriert neue RFID-Chips |
| `RFID_TAG_ASSIGNER` | `C` | Ordnet RFID-Chips Kunden zu |
| `GATE_KEEPER_IN` | `GI` | Werkstatt-Eingangstor |
| `GATE_KEEPER_OUT` | `GO` | Werkstatt-Ausgangstor |
| `SWITCH_BOX` | `S` | Steuert Werkzeugstrom (gekoppelt mit Werkzeug) |

#### Kommandos

| Kommando                       | Felder                                                         |
|--------------------------------|----------------------------------------------------------------|
| _(Erstellung über MacAddress)_ | macAddress (`MacAddress`-Wertobjekt)                           |
| `UpdateRfidReaderCommand`      | id (`RfidReaderId`), type (`RfidReaderType`), name, socketName |

#### Geschäftsregeln / Invarianten

| #   | Regel                                                      | Durchgesetzt in                                                          |
|-----|------------------------------------------------------------|--------------------------------------------------------------------------|
| R-1 | `macAddress` muss über alle RFID-Lesegeräte eindeutig sein | DB-Unique-Constraint, **TODO** in `RfidReaderService.createRfidReader()` |
| R-2 | `macAddress` darf nicht leer sein                          | Bean Validation                                                          |

#### Domänenereignisse

| Ereignis              | Payload                          |
|-----------------------|----------------------------------|
| `RFID_READER_CREATED` | Vollständiger Lesegerät-Snapshot |
| `RFID_READER_UPDATED` | Aktualisierte Felder             |
| `RFID_READER_DELETED` | rfidReaderId                     |

---

### 3.4 RfidTag (RFID-Chip)

**Paket:** `de.schaffbar.core_pos.rfid_tag`

**Aggregat-Root:** `RfidTag`

**Identität:** `RfidTagId` (String — Hardware-ID)

#### Attribute

| Feld        | Typ       | Constraints                    |
|-------------|-----------|--------------------------------|
| `id`        | `String`  | PK (Hardware-Chip-ID)          |
| `active`    | `boolean` | Standard `true` bei Erstellung |
| `createdAt` | `Instant` | `@NotNull`                     |
| `updatedAt` | `Instant` | `@Version`                     |

#### Kommandos

| Kommando               | Felder             |
|------------------------|--------------------|
| `CreateRfidTagCommand` | rfidTagId (String) |

#### Geschäftsregeln / Invarianten

| #    | Regel                                | Durchgesetzt in                                  |
|------|--------------------------------------|--------------------------------------------------|
| RT-1 | RFID-Chip-ID darf nicht leer sein    | `@NotBlank` auf `CreateRfidTagCommand.rfidTagId` |
| RT-2 | Neu erstellte Chips sind immer aktiv | `RfidTag.of()` setzt `active = true`             |

#### Domänenereignisse

| Ereignis           | Payload                     |
|--------------------|-----------------------------|
| `RFID_TAG_CREATED` | Vollständiger Chip-Snapshot |
| `RFID_TAG_DELETED` | rfidTagId                   |

---

### 3.5 RfidTagAssignment (RFID-Chip-Zuordnung)

**Paket:** `de.schaffbar.core_pos.rfid_tag_assignment`

**Aggregat-Root:** `RfidTagAssignment`

**Identität:** `RfidTagAssignmentId` (UUID)

#### Attribute

| Feld             | Typ                       | Constraints                                           |
|------------------|---------------------------|-------------------------------------------------------|
| `id`             | `UUID`                    | PK                                                    |
| `customerId`     | `UUID`                    | `@NotNull`, `@Column(unique = true)`                  |
| `assignmentType` | `RfidTagAssignmentType`   | `@NotNull` (`FIXED` oder `TEMPORARY`)                 |
| `status`         | `RfidTagAssignmentStatus` | `@NotNull` (`WAITING_FOR_ASSIGNMENT` oder `ASSIGNED`) |
| `rfidTagId`      | `String`                  | `@Column(unique = true)`, gesetzt bei Zuordnung       |
| `assignmentDate` | `Instant`                 | gesetzt bei Zuordnung                                 |
| `updatedAt`      | `Instant`                 | `@Version`                                            |

**Lebenszyklus:** `WAITING_FOR_ASSIGNMENT` → `ASSIGNED` → _(gelöscht bei Aufhebung)_

#### Kommandos

| Operation           | Eingabe                    |
|---------------------|----------------------------|
| Zuordnung anfordern | customerId, assignmentType |
| RFID-Chip zuordnen  | rfidTagId                  |
| Zuordnung aufheben  | customerId                 |

#### Geschäftsregeln / Invarianten

| #    | Regel                                                                                             | Durchgesetzt in                                                                                              |
|------|---------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------|
| TA-1 | Ein Kunde kann höchstens eine aktive RFID-Chip-Zuordnung haben                                    | `@Column(unique = true)` auf `customerId` + Prüfung in `RfidTagAssignmentService.requestRfidTagAssignment()` |
| TA-2 | Ein RFID-Chip kann höchstens einem Kunden zugeordnet sein                                         | `@Column(unique = true)` auf `rfidTagId` + Prüfung in `RfidTagAssignmentService.assignRfidTag()`             |
| TA-3 | Es darf nur eine ausstehende Zuordnung (`WAITING_FOR_ASSIGNMENT`) gleichzeitig existieren         | `RfidTagAssignmentService.requestRfidTagAssignment()`                                                        |
| TA-4 | Die Zuordnung kann nur im Status `WAITING_FOR_ASSIGNMENT` erfolgen                                | **TODO** — vermerkt in `RfidTagAssignment.assignRfidTag()`                                                   |
| TA-5 | Nur `ASSIGNED`-Zuordnungen können aufgehoben werden                                               | `RfidTagAssignmentService.unassignRfidTag()` filtert nach `isAssigned()`                                     |
| TA-6 | Bei Aufhebung wird der Zuordnungsdatensatz gelöscht (kein Soft-Delete) und eine Historie erstellt | `CustomerUnassignRfidTag`-Anwendungsfall                                                                     |
| TA-7 | Der Kunde muss existieren, bevor eine Zuordnung angefordert wird                                  | `CustomerRequestRfidTagAssignment`-Anwendungsfall                                                            |
| TA-8 | Der RFID-Chip muss existieren, bevor er zugeordnet werden kann                                    | `CustomerAssignRfidTag`-Anwendungsfall                                                                       |

#### Domänenereignisse

| Ereignis                        | Payload             |
|---------------------------------|---------------------|
| `RFID_TAG_ASSIGNMENT_REQUESTED` | Zuordnungs-Snapshot |
| `RFID_TAG_ASSIGNED`             | Zuordnungs-Snapshot |
| `RFID_TAG_UNASSIGNED`           | Zuordnungs-Snapshot |

---

### 3.6 RfidTagAssignmentHistory (Zuordnungshistorie)

**Paket:** `de.schaffbar.core_pos.rfid_tag_assignment_history`

**Aggregat-Root:** `RfidTagAssignmentHistory`

**Identität:** `RfidTagAssignmentId` (UUID) — _Hinweis: FIXME im Code schlägt Umbenennung
zu `RfidTagAssignmentHistoryId` vor_

#### Attribute

| Feld               | Typ                     | Constraints                  |
|--------------------|-------------------------|------------------------------|
| `id`               | `UUID`                  | PK                           |
| `customerId`       | `UUID`                  | `@NotNull`                   |
| `rfidTagId`        | `String`                | `@NotNull`                   |
| `assignmentType`   | `RfidTagAssignmentType` | `@NotNull`                   |
| `assignmentDate`   | `Instant`               | `@NotNull`, `@Past`          |
| `unassignmentDate` | `Instant`               | `@NotNull`, `@PastOrPresent` |
| `updatedAt`        | `Instant`               | `@Version`                   |

#### Geschäftsregeln / Invarianten

| #     | Regel                                                                       | Durchgesetzt in                                      |
|-------|-----------------------------------------------------------------------------|------------------------------------------------------|
| TAH-1 | Historie wird aus einer zuvor aktiven (ASSIGNED) RfidTagAssignment erstellt | `RfidTagAssignmentHistory.of(RfidTagAssignmentView)` |
| TAH-2 | `assignmentDate` muss in der Vergangenheit liegen                           | `@Past`-Validierung                                  |
| TAH-3 | `unassignmentDate` muss in der Vergangenheit oder Gegenwart liegen          | `@PastOrPresent`-Validierung                         |
| TAH-4 | Historieneinträge sind unveränderlich — keine Update-Kommandos              | Keine Command-Methoden                               |

#### Domänenereignisse

_Keine — dies ist ein Projektions-/Audit-Log-Aggregat._

---

### 3.7 WorkshopSession (Werkstatt-Sitzung)

**Paket:** `de.schaffbar.core_pos.workshop_session`

**Aggregat-Root:** `WorkshopSession`

**Identität:** `WorkshopSessionId` (UUID)

#### Attribute

| Feld         | Typ                     | Constraints                      |
|--------------|-------------------------|----------------------------------|
| `id`         | `UUID`                  | PK                               |
| `customerId` | `UUID`                  | `@NotNull`                       |
| `startTime`  | `Instant`               | `@NotNull`                       |
| `closeTime`  | `Instant`               | optional, gesetzt beim Schließen |
| `status`     | `WorkshopSessionStatus` | `@NotNull` (`OPEN` oder `PAID`)  |
| `updatedAt`  | `Instant`               | `@Version`                       |

**Lebenszyklus:** `OPEN` → `PAID`

#### Kommandos

| Operation         | Eingabe    |
|-------------------|------------|
| Sitzung starten   | customerId |
| Sitzung schließen | customerId |

#### Geschäftsregeln / Invarianten

| #    | Regel                                                                   | Durchgesetzt in                                                               |
|------|-------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| WS-1 | Ein Kunde kann höchstens eine `OPEN`-Sitzung gleichzeitig haben         | `WorkshopSessionService.startSession()` — prüft auf bestehende offene Sitzung |
| WS-2 | Schließen setzt den Status auf `PAID` und erfasst `closeTime`           | `WorkshopSession.close()`                                                     |
| WS-3 | Der Kunde muss existieren, bevor eine Sitzung geschlossen wird          | `CloseSession`-Anwendungsfall                                                 |
| WS-4 | Eine nicht existierende Sitzung kann nicht geschlossen werden           | `WorkshopSessionService.closeSession()`                                       |
| WS-5 | Ausstehende Werkstattnutzungen sollten vor dem Schließen geprüft werden | **TODO** — vermerkt im `CloseSession`-Anwendungsfall                          |

#### Domänenereignisse

| Ereignis                   | Payload           |
|----------------------------|-------------------|
| `WORKSHOP_SESSION_STARTED` | Sitzungs-Snapshot |
| `WORKSHOP_SESSION_CLOSED`  | Sitzungs-Snapshot |

---

### 3.8 WorkshopUsage (Werkstatt-Nutzung)

**Paket:** `de.schaffbar.core_pos.workshop_usage`

**Aggregat-Root:** `WorkshopUsage`

**Identität:** `WorkshopUsageId` (UUID)

_Hinweis: Im Code wird eine Umbenennung zu `WorkshopSlot` oder `UsageSlot` erwogen._

#### Attribute

| Feld                | Typ       | Constraints                     |
|---------------------|-----------|---------------------------------|
| `id`                | `UUID`    | PK                              |
| `customerId`        | `UUID`    | `@NotNull`                      |
| `workshopSessionId` | `UUID`    | `@NotNull`                      |
| `entryTime`         | `Instant` | `@NotNull`                      |
| `exitTime`          | `Instant` | optional, gesetzt beim Austritt |
| `updatedAt`         | `Instant` | `@Version`                      |

#### Kommandos

| Operation           | Eingabe                       |
|---------------------|-------------------------------|
| Werkstatt betreten  | customerId, workshopSessionId |
| Werkstatt verlassen | customerId, workshopSessionId |

#### Geschäftsregeln / Invarianten

| #    | Regel                                                                                      | Durchgesetzt in                                                                                  |
|------|--------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| WU-1 | Ein Kunde kann höchstens eine aktive Werkstattnutzung (ohne `exitTime`) gleichzeitig haben | `WorkshopUsageService.enterWorkshop()` — prüft auf offene Nutzung via `findActiveByCustomerId()` |
| WU-2 | Der Kunde muss eine aktive Nutzung haben, um die Werkstatt zu verlassen                    | `WorkshopUsageService.leaveWorkshop()` — wirft `NoActiveWorkshopUsageFoundException`             |
| WU-3 | Die Dauer wird als `exitTime - entryTime` berechnet (auf Sekunden gekürzt)                 | `WorkshopUsage.getDuration()` — gibt `null` zurück, wenn einer der Zeitstempel fehlt             |
| WU-4 | Vor dem Betreten muss eine Werkstatt-Sitzung existieren (OPEN)                             | `EnterWorkshop`-Anwendungsfall — erstellt Sitzung falls nicht vorhanden                          |
| WU-5 | Aktive Werkzeugnutzungen werden beim Verlassen der Werkstatt automatisch gestoppt          | `LeaveWorkshop`-Anwendungsfall — ruft `ToolUsageService.stopAllUsagesForCustomer()` auf          |
| WU-6 | Der Kunde muss ein gültiges Zertifikat zum Betreten haben                                  | **TODO** — vermerkt im `EnterWorkshop`-Anwendungsfall                                            |

#### Domänenereignisse

| Ereignis                 | Payload           |
|--------------------------|-------------------|
| `WORKSHOP_USAGE_ENTERED` | Nutzungs-Snapshot |
| `WORKSHOP_USAGE_LEFT`    | Nutzungs-Snapshot |

---

### 3.9 ToolUsage (Werkzeugnutzung)

**Paket:** `de.schaffbar.core_pos.tool_usage`

**Aggregat-Root:** `ToolUsage`

**Identität:** `ToolUsageId` (UUID)

#### Attribute

| Feld                | Typ       | Constraints                    |
|---------------------|-----------|--------------------------------|
| `id`                | `UUID`    | PK                             |
| `customerId`        | `UUID`    | `@NotNull`                     |
| `toolId`            | `UUID`    | `@NotNull`                     |
| `workshopSessionId` | `UUID`    | `@NotNull`                     |
| `startTime`         | `Instant` | `@NotNull`                     |
| `endTime`           | `Instant` | optional, gesetzt beim Stoppen |
| `updatedAt`         | `Instant` | `@Version`                     |

#### Kommandos

| Kommando                | Felder                                |
|-------------------------|---------------------------------------|
| `StartToolUsageCommand` | customerId, toolId, workshopSessionId |

#### Geschäftsregeln / Invarianten

| #    | Regel                                                                                                              | Durchgesetzt in                                                                                       |
|------|--------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| TU-1 | Ein Kunde kann maximal 2 Werkzeuge gleichzeitig nutzen                                                             | `ToolUsageService.startUsage()` — prüft Anzahl via `countActiveByCustomerId()`                        |
| TU-2 | Ein Kunde kann Werkzeuge nur nutzen, wenn er sich aktiv in der Werkstatt befindet (aktive WorkshopUsage)           | `StartToolUsage`-Anwendungsfall — ruft `verifyCustomerIsInWorkshop()` auf                             |
| TU-3 | Ein Kunde kann dasselbe Werkzeug nicht doppelt gleichzeitig nutzen                                                 | `ToolUsageService.startUsage()` — prüft via `findActiveByCustomerIdAndToolId()`                       |
| TU-4 | Beim Verlassen der Werkstatt werden alle aktiven Werkzeugnutzungen automatisch gestoppt                            | `LeaveWorkshop`-Anwendungsfall — ruft `ToolUsageService.stopAllUsagesForCustomer()` auf               |
| TU-5 | Die Werkzeug-Ein-/Ausschalterkennung erfolgt über RFID-Lesegerät (Typ SWITCH_BOX), das dem Werkzeug zugeordnet ist | `DeviceController` — löst Werkzeug via `ToolService.getTool(RfidReaderId)` auf und toggelt Start/Stop |
| TU-6 | Die Kundenidentifikation erfolgt über die RFID-Chip-Zuordnung                                                      | `DeviceController` — löst Kunde via `RfidTagAssignmentService` auf                                    |
| TU-7 | Die Dauer wird als `endTime - startTime` berechnet (auf Sekunden gekürzt)                                          | `ToolUsage.getDuration()` — gibt `null` zurück, wenn einer der Zeitstempel fehlt                      |
| TU-8 | Der Kunde muss eine offene Werkstatt-Sitzung haben, um eine Werkzeugnutzung zu starten                             | `StartToolUsage`-Anwendungsfall — prüft auf aktive Sitzung                                            |
| TU-9 | Ein Werkzeug kann gleichzeitig nur von einem Kunden genutzt werden                                                 | `ToolUsageService.startUsage()` — prüft via `findActiveByToolId()`, wirft `ToolAlreadyInUseException` |

#### Domänenereignisse

| Ereignis             | Payload                   |
|----------------------|---------------------------|
| `TOOL_USAGE_STARTED` | Werkzeugnutzungs-Snapshot |
| `TOOL_USAGE_STOPPED` | Werkzeugnutzungs-Snapshot |

---

## 4. Anwendungsfälle (Application Services)

Anwendungsfälle befinden sich im Paket `use_case` und **orchestrieren Operationen über mehrere Aggregate hinweg**. Sie
sind der einzige Ort, an dem aggregatübergreifende Koordination stattfindet.

| Anwendungsfall                     | Beschreibung                                                                                                   | Beteiligte Aggregate                                  |
|------------------------------------|----------------------------------------------------------------------------------------------------------------|-------------------------------------------------------|
| `EnterWorkshop`                    | Kunde betritt die Werkstatt. Erstellt eine Sitzung falls keine existiert, dann erstellt einen Nutzungseintrag. | WorkshopSession, WorkshopUsage                        |
| `LeaveWorkshop`                    | Kunde verlässt die Werkstatt. Stoppt alle aktiven Werkzeugnutzungen, dann erfasst Austrittszeit.               | WorkshopSession, WorkshopUsage, ToolUsage             |
| `CloseSession`                     | Schließt die Werkstatt-Sitzung eines Kunden (als PAID markieren).                                              | Customer, WorkshopSession                             |
| `StartToolUsage`                   | Startet die Werkzeugnutzung für einen Kunden. Prüft Werkstatt-Anwesenheit und offene Sitzung.                  | ToolUsage, WorkshopSession, WorkshopUsage             |
| `StopToolUsage`                    | Stoppt die Werkzeugnutzung für einen Kunden an einem bestimmten Werkzeug.                                      | ToolUsage                                             |
| `CustomerRequestRfidTagAssignment` | Startet den RFID-Chip-Zuordnungsprozess für einen Kunden.                                                      | Customer, RfidTagAssignment                           |
| `CustomerAssignRfidTag`            | Vervollständigt die RFID-Chip-Zuordnung durch Verknüpfung des Chips mit der ausstehenden Zuordnung.            | RfidTag, RfidTagAssignment                            |
| `CustomerUnassignRfidTag`          | Entfernt den RFID-Chip vom Kunden, verschiebt den Datensatz in die Historie.                                   | Customer, RfidTagAssignment, RfidTagAssignmentHistory |
| `ToolCreate`                       | Erstellt ein Werkzeug mit optionaler RFID-Lesegerät-Validierung (muss SWITCH_BOX sein).                        | RfidReader, Tool                                      |
| `ToolAssignRfidReader`             | Ordnet ein RFID-Lesegerät einem Werkzeug zu (Lesegerät muss SWITCH_BOX sein).                                  | RfidReader, Tool                                      |

---

## 5. Lesemodelle (Query Services)

| Dienst                     | Endpunkt-Konzept       | Beschreibung                                                                                                                                                   |
|----------------------------|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `WorkshopDashboardService` | Aktive Werkstattnutzer | Aggregatübergreifende Abfrage, die aktive `WorkshopUsage`-Einträge mit `Customer`-Daten kombiniert, um anzuzeigen, wer sich aktuell in der Werkstatt befindet. |

Die `WorkshopDashboardEntryView` liefert:

- `customerId`, `firstName`, `lastName`, `entryTime`

---

## 6. Wertobjekte (Value Objects)

Alle Wertobjekte befinden sich in `de.schaffbar.core_pos.shared.id`:

| Wertobjekt            | Umschließt | Verwendet für                     |
|-----------------------|------------|-----------------------------------|
| `CustomerId`          | `UUID`     | Kundenidentität                   |
| `ToolId`              | `UUID`     | Werkzeugidentität                 |
| `ToolUsageId`         | `UUID`     | Werkzeugnutzungsidentität         |
| `RfidReaderId`        | `UUID`     | RFID-Lesegerät-Identität          |
| `RfidTagId`           | `String`   | RFID-Chip-Identität (Hardware-ID) |
| `RfidTagAssignmentId` | `UUID`     | RFID-Chip-Zuordnungsidentität     |
| `WorkshopSessionId`   | `UUID`     | Werkstatt-Sitzungsidentität       |
| `WorkshopUsageId`     | `UUID`     | Werkstatt-Nutzungsidentität       |
| `MacAddress`          | `String`   | RFID-Lesegerät-MAC-Adresse        |

---

## 7. Domänenereignisse (Domain Events)

### Ereignis-Infrastruktur

- **`SchaffbarEvent`** — Hülle mit `id`, `type`, `version`, `aggregateType`, `aggregateId`, `timestamp`, `payload` (
  JSON), `owner`
- **`OutboxEvent`** — JPA-Entität, persistiert in der `schaffbar.outbox_event`-Tabelle innerhalb derselben Transaktion
- **`EventPayload`** — Marker-Interface mit Validierungsunterstützung
- **`AggregateType`** — Enumeration, die das Quell-Aggregat identifiziert

### Ereignisfluss

```
Command-Methode (Aggregat-Root)
  └── Gibt List<SchaffbarEvent> zurück
        └── Service speichert in OutboxEventRepository
              └── outbox_event-Tabelle (Transaktionale-Outbox-Muster)
                    └── Wird von externem Prozess abgeholt/veröffentlicht
```

### Muster zur Ereigniserstellung

```
EventFactory.einEreignis(aggregat)
  ├── PayloadMapper.MAPPER.zuPayload(aggregat)         // MapStruct-Mapping
  ├── payload.validate()                                 // Bean Validation
  └── SchaffbarEvent.<aggregatTyp>Event(typ, id, payload)  // Hüllen-Erstellung
```

### Vollständige Ereignisliste

| Aggregat          | Ereignistyp                                                                                                                         |
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

## Anhang: Bekannte TODOs und offene Designfragen

| Stelle                                 | Beschreibung                                                                                |
|----------------------------------------|---------------------------------------------------------------------------------------------|
| `CustomerService.deleteCustomer()`     | Löschkonzept erforderlich — RFID-Chip-Zuordnung vor dem Löschen prüfen                      |
| `RfidReaderService.createRfidReader()` | Prüfung auf doppelte MAC-Adresse bei Erstellung                                             |
| `RfidTagAssignment.assignRfidTag()`    | Validieren, dass die Zuordnung im Status `WAITING_FOR_ASSIGNMENT` ist                       |
| `EnterWorkshop.process()`              | Prüfen, ob der Kunde ein gültiges Zertifikat zum Betreten hat                               |
| `CloseSession.process()`               | Ausstehende Werkstattnutzungen vor dem Schließen prüfen                                     |
| `WorkshopUsage`                        | Umbenennung zu `WorkshopSlot` oder `UsageSlot` erwägen                                      |
| `RfidTagAssignmentHistory`             | Umbenennung des ID-Typs zu `RfidTagAssignmentHistoryId` erwägen                             |
| `RfidTagCommands.CreateRfidTagCommand` | `rfidTagId` auf `RfidTagId`-Wertobjekt umstellen                                            |
| `*Service.saveOutboxEvents()`          | DRY — Methode ist in allen Services dupliziert, in Basisklasse oder Hilfsklasse extrahieren |
| `WorkshopSessionService`               | Namensinkonsistenz — einheitlich entweder „active" oder „open" für Sitzungen verwenden      |
| `EventType`                            | Umbenennung von `WORKSHOP_USAGE_ENTERED` / `LEFT` zu `WORKSHOP_ENTERED` / `EXITED` erwägen  |
