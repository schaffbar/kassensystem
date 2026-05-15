# Schaffbar - Dokumentation

## Inhaltsverzeichnis

1. [Domänenübersicht](#1-domänenübersicht)
2. [Aggregat-Strukturmuster](#2-aggregat-strukturmuster)
3. [Aggregat-Katalog](#3-aggregat-katalog)
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
- **Einweisungsverwaltung** — Zertifizierungen pro Kunde und Werkzeug, die den Zugang zu Maschinen regeln (ACTIVE/PAUSED/REVOKED-Lebenszyklus)
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

Die detaillierte Dokumentation jedes Aggregats (Attribute, Kommandos, Geschäftsregeln, Domänenereignisse) befindet sich
in einer `README.md` innerhalb des jeweiligen Pakets. So bleibt die Dokumentation nah am Code, den sie beschreibt.

| #    | Aggregat                                        | Paket                                                | Identität                    | README                                                                                                                |
|------|-------------------------------------------------|------------------------------------------------------|------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| 3.1  | Customer (Kunde)                                | `de.schaffbar.core_pos.customer`                     | `CustomerId` (UUID)          | [customer/README.md](../src/main/java/de/schaffbar/core_pos/customer/README.md)                                       |
| 3.2  | Tool (Werkzeug)                                 | `de.schaffbar.core_pos.tool`                         | `ToolId` (UUID)              | [tool/README.md](../src/main/java/de/schaffbar/core_pos/tool/README.md)                                               |
| 3.3  | RfidReader (RFID-Lesegerät)                     | `de.schaffbar.core_pos.rfid_reader`                  | `RfidReaderId` (UUID)        | [rfid_reader/README.md](../src/main/java/de/schaffbar/core_pos/rfid_reader/README.md)                                 |
| 3.4  | RfidTag (RFID-Chip)                             | `de.schaffbar.core_pos.rfid_tag`                     | `RfidTagId` (String)         | [rfid_tag/README.md](../src/main/java/de/schaffbar/core_pos/rfid_tag/README.md)                                       |
| 3.5  | RfidTagAssignment (RFID-Chip-Zuordnung)         | `de.schaffbar.core_pos.rfid_tag_assignment`          | `RfidTagAssignmentId` (UUID) | [rfid_tag_assignment/README.md](../src/main/java/de/schaffbar/core_pos/rfid_tag_assignment/README.md)                 |
| 3.6  | RfidTagAssignmentHistory (Zuordnungshistorie)   | `de.schaffbar.core_pos.rfid_tag_assignment_history`  | `RfidTagAssignmentId` (UUID) | [rfid_tag_assignment_history/README.md](../src/main/java/de/schaffbar/core_pos/rfid_tag_assignment_history/README.md) |
| 3.7  | WorkshopSession (Werkstatt-Sitzung)             | `de.schaffbar.core_pos.workshop_session`             | `WorkshopSessionId` (UUID)   | [workshop_session/README.md](../src/main/java/de/schaffbar/core_pos/workshop_session/README.md)                       |
| 3.8  | WorkshopUsage (Werkstatt-Nutzung)               | `de.schaffbar.core_pos.workshop_usage`               | `WorkshopUsageId` (UUID)     | [workshop_usage/README.md](../src/main/java/de/schaffbar/core_pos/workshop_usage/README.md)                           |
| 3.9  | ToolUsage (Werkzeugnutzung)                     | `de.schaffbar.core_pos.tool_usage`                   | `ToolUsageId` (UUID)         | [tool_usage/README.md](../src/main/java/de/schaffbar/core_pos/tool_usage/README.md)                                   |
| 3.10 | ToolCertification (Werkzeug-Einweisung)         | `de.schaffbar.core_pos.tool_certification`           | `ToolCertificationId` (UUID) | [tool_certification/README.md](../src/main/java/de/schaffbar/core_pos/tool_certification/README.md)                   |


---

## 4. Anwendungsfälle (Application Services)

Anwendungsfälle befinden sich im Paket `use_case` und **orchestrieren Operationen über mehrere Aggregate hinweg**. Sie
sind der einzige Ort, an dem aggregatübergreifende Koordination stattfindet.

| Anwendungsfall                     | Beschreibung                                                                                                   | Beteiligte Aggregate                                  |
|------------------------------------|----------------------------------------------------------------------------------------------------------------|-------------------------------------------------------|
| `EnterWorkshop`                    | Kunde betritt die Werkstatt. Erstellt eine Sitzung falls keine existiert, dann erstellt einen Nutzungseintrag. | WorkshopSession, WorkshopUsage                        |
| `LeaveWorkshop`                    | Kunde verlässt die Werkstatt. Stoppt alle aktiven Werkzeugnutzungen, dann erfasst Austrittszeit.               | WorkshopSession, WorkshopUsage, ToolUsage             |
| `CloseSession`                     | Schließt die Werkstatt-Sitzung eines Kunden (als PAID markieren).                                              | Customer, WorkshopSession                             |
| `StartToolUsage`                   | Startet die Werkzeugnutzung für einen Kunden. Prüft Einweisung, Werkstatt-Anwesenheit und offene Sitzung. | ToolUsage, WorkshopSession, WorkshopUsage, ToolCertification |
| `StopToolUsage`                    | Stoppt die Werkzeugnutzung für einen Kunden an einem bestimmten Werkzeug.                                      | ToolUsage                                             |
| `CertifyCustomersForTool`          | Erstellt Einweisungen für mehrere Kunden nach einer Schulung. Unterstützt Teilerfolge mit Fehlerbericht.       | ToolCertification                                     |
| `PauseToolCertification`           | Pausiert die Einweisung eines Kunden.                                                                          | ToolCertification                                     |
| `ReactivateToolCertification`      | Reaktiviert eine pausierte Einweisung.                                                                         | ToolCertification                                     |
| `RevokeToolCertification`          | Entzieht eine Einweisung endgültig.                                                                            | ToolCertification                                     |
| `CustomerRequestRfidTagAssignment` | Startet den RFID-Chip-Zuordnungsprozess für einen Kunden.                                                      | Customer, RfidTagAssignment                           |
| `CustomerAssignRfidTag`            | Vervollständigt die RFID-Chip-Zuordnung durch Verknüpfung des Chips mit der ausstehenden Zuordnung.            | RfidTag, RfidTagAssignment                            |
| `CustomerUnassignRfidTag`          | Entfernt den RFID-Chip vom Kunden, verschiebt den Datensatz in die Historie.                                   | Customer, RfidTagAssignment, RfidTagAssignmentHistory |
| `ToolCreate`                       | Erstellt ein Werkzeug mit optionaler RFID-Lesegerät-Validierung (muss SWITCH_BOX sein).                        | RfidReader, Tool                                      |
| `ToolAssignRfidReader`             | Ordnet ein RFID-Lesegerät einem Werkzeug zu (Lesegerät muss SWITCH_BOX sein).                                  | RfidReader, Tool                                      |
| `ChangeRfidReaderType`             | Ändert den Typ eines RFID-Lesegeräts. Wenn aktueller Typ SWITCH_BOX und Werkzeug zugeordnet → Fehler.          | RfidReader, Tool                                      |

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
| `ToolCertificationId` | `UUID`     | Werkzeug-Einweisungsidentität     |
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
| ToolCertification | `TOOL_CERTIFICATION_CREATED`, `TOOL_CERTIFICATION_PAUSED`, `TOOL_CERTIFICATION_REACTIVATED`, `TOOL_CERTIFICATION_REVOKED`             |
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
| `EnterWorkshop.process()`              | ~~Prüfen, ob der Kunde ein gültiges Zertifikat zum Betreten hat~~ — gelöst über Einweisungsprüfung in `StartToolUsage` |
| `CloseSession.process()`               | Ausstehende Werkstattnutzungen vor dem Schließen prüfen                                     |
| `WorkshopUsage`                        | Umbenennung zu `WorkshopSlot` oder `UsageSlot` erwägen                                      |
| `RfidTagAssignmentHistory`             | Umbenennung des ID-Typs zu `RfidTagAssignmentHistoryId` erwägen                             |
| `RfidTagCommands.CreateRfidTagCommand` | `rfidTagId` auf `RfidTagId`-Wertobjekt umstellen                                            |
| `*Service.saveOutboxEvents()`          | DRY — Methode ist in allen Services dupliziert, in Basisklasse oder Hilfsklasse extrahieren |
| `WorkshopSessionService`               | Namensinkonsistenz — einheitlich entweder „active" oder „open" für Sitzungen verwenden      |
| `EventType`                            | Umbenennung von `WORKSHOP_USAGE_ENTERED` / `LEFT` zu `WORKSHOP_ENTERED` / `EXITED` erwägen  |
