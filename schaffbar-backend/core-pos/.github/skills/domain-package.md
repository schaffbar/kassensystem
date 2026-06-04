# Skill: Domain Package erstellen (Schaffbar Core-POS)

## Wann nutzen

Wenn ein neues Domain-Package (Aggregate) im Schaffbar Core-POS Projekt erstellt werden soll – analog zu `tool`,
`tool_certification`, `tool_usage`, `customer`, etc.

## Projekt-Kontext

- **Basis-Package:** `de.schaffbar.core_pos`
- **DB-Schema:** `SCHAFFBAR`
- **Build:** Maven, Java 21+, Spring Boot, JPA/Hibernate, MapStruct, Lombok, Jakarta Validation
- **Architektur:** DDD mit Transactional Outbox Pattern für Domain Events
- **Dokumentation:** `docs/ddd-overview.md` (EN) und `docs/ddd-uebersicht.md` (DE)

## Checkliste – Dateien und Reihenfolge

### 1. Value Object (shared/id)

- `<Aggregate>Id.java` in `shared/id/` — nach dem Muster von `ToolId` / `ToolCertificationId` / `ToolUsageId`:
    - `@Value(staticConstructor = "of")`, `@JsonValue`, `@JsonCreator`
    - `random()` Factory-Methode
    - `sameValueAs()` Vergleichsmethode
- `TOOL_CERTIFICATION_ID` o.ä. zum `ValueObjectAssert.ValueObject` Enum hinzufügen

### 2. Converter (shared/util)

- `StringTo<Aggregate>IdConverter.java` — `implements Converter<String, <Aggregate>Id>`, `@Component`
- Notwendig für `@PathVariable` und `@RequestParam` Deserialisierung
- **Für JEDES Value Object das in `@PathVariable` oder `@RequestParam` verwendet wird, MUSS ein Converter existieren**
- Nicht nur für IDs — auch für andere Value Objects wie `MacAddress`, `IpAddress`, etc.
- Pattern: `return <ValueObject>.of(source)` im `convert()`-Method

### 3. Event-Infrastruktur (shared/event)

- `AggregateType` Enum — neuen Wert hinzufügen
- `EventType` Enum — alle Event-Typen des neuen Aggregats hinzufügen
- `<Aggregate>EventPayload.java` in `shared/event/payload/` — Interface `extends EventPayload` mit **nested record
  payloads**
    - Beispiel: `record <Aggregate>CreatedPayload(...) implements <Aggregate>EventPayload {}`
    - **Verwende immer Value Objects** (`CustomerId`, `ToolId`, etc.) für ID-Referenzen, niemals `UUID`/`String`
- `SchaffbarEvent.java` — neue statische Factory-Methode
  `<aggregate>Event(EventType, <Aggregate>Id, <Aggregate>EventPayload)` hinzufügen + Import für Payload und Id

### 4. Exceptions (shared/exception)

- Domain-spezifische Exceptions nach Bedarf in `shared/exception/`
- Pattern: `extends RuntimeException`, `@Serial`, statische `MESSAGE`-Konstante, Konstruktor mit Value Objects
- Beispiel-Naming: `CustomerAlreadyInstructorException`, `CustomerNotInstructorException`,
  `MacAddressAlreadyUsedException`, `NoRfidTagAssignedException`
- Keine generischen `IllegalStateException` oder `RuntimeException` verwenden — **immer** eigene Exception-Klasse
  erstellen
- `ResourceNotFoundException` — neue Factory-Methode + neuen `Resource` Enum-Wert hinzufügen falls nötig

### 5. Aggregate-Package (`<aggregate_name>/`)

Alle folgenden Dateien sind **package-private** (außer Service, Views, Commands die `public` sind):

#### 5a. Status/Type Enum

- `<Aggregate>Status.java` (falls Lifecycle vorhanden) — `public enum`

#### 5b. Commands

- `<Aggregate>Commands.java` — `public interface` mit `record`-Typen
- Bean Validation Annotationen (`@Valid`, `@NotNull`, `@NotEmpty`, etc.)
- Verwende Value Objects als Feld-Typen
- **Batch-Commands** mit `List<@NotNull @Valid ValueObject>` für Operationen auf mehreren Einträgen gleichzeitig (z.B.
  `AddInstructorsCommand(List<CustomerId>)`)

#### 5c. Aggregate Root (Entity)

- `<Aggregate>.java` — JPA `@Entity`, `@Table(name = "...", schema = "SCHAFFBAR")`
- Lombok: `@Getter`, `@Setter(AccessLevel.PRIVATE)`, `@NoArgsConstructor(access = AccessLevel.PROTECTED)`, `@ToString`
- **`@Setter` IMMER mit `AccessLevel.PRIVATE`** — niemals ohne Access Level
- Felder intern als Raw-Typen (`UUID`, `String`), Getter geben Value Objects zurück
- `@Id` auf UUID, `@Version` auf `Instant updatedAt`, `@Enumerated(EnumType.STRING)` für Enums
- **ID-Getter heißt `getId()`** und gibt das Value Object zurück (z.B. `ToolId`, `CustomerId`)
    - Nicht `getToolId()` oder `getCustomerId()` — immer `getId()` für die eigene Identity
    - Referenz-IDs zu anderen Aggregaten (z.B. `customerId` in `ToolCertification`) behalten ihren vollen Namen
- **Statische Factory `of(CreateCommand)`** gibt **direkt die Entity** zurück (kein `WithEvents`-Record). Das
  Creation-Event wird im Service erzeugt.
- In `of()`: `<Aggregate>Id.random().getValue()` verwenden, NICHT `UUID.randomUUID()`
- **Command-Methoden** (`pause()`, `revoke()`, etc.) geben immer `List<SchaffbarEvent>` zurück
- Zustandsvalidierung in Command-Methoden, nicht im Service
- Null-Checks mit `Objects.nonNull()` / `Objects.isNull()`, niemals `!= null`

#### 5d. Repository

- `<Aggregate>Repository.java` — `interface extends JpaRepository<Entity, UUID>`, `@Repository`, package-private
- Custom Query-Methoden nach Spring Data Naming Convention

#### 5e. PayloadMapper

- `<Aggregate>PayloadMapper.java` — MapStruct `@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR)`, package-private
- Singleton: `MAPPER = Mappers.getMapper(...)`
- Wenn Payload und Entity dieselben Value Objects nutzen: keine `default map()`-Methoden nötig
- Nur `default map()`-Methoden hinzufügen wenn Typ-Konvertierung nötig ist (z.B. `CustomerId → UUID`)

#### 5f. EventFactory

- `<Aggregate>EventFactory.java` — `final class`, `@NoArgsConstructor(access = AccessLevel.PRIVATE)`, package-private
- Eine statische Methode pro Event: mappt via PayloadMapper, validiert Payload, gibt `SchaffbarEvent` zurück
- **Jede semantisch unterschiedliche Aktion bekommt ein eigenes Event** — niemals das gleiche Event für
  Set- und Clear-Operationen verwenden. Beispiel: `TOOL_WLAN_RELAIS_SET` + `TOOL_WLAN_RELAIS_CLEARED` statt
  eines generischen `TOOL_WLAN_RELAIS_UPDATED`

#### 5g. Views

- `<Aggregate>Views.java` — MapStruct `@Mapper`, `public interface`
- Singleton: `MAPPER = Mappers.getMapper(...)`
- View-Records mit Value Objects als Feld-Typen
- **`@Mapping(target = "id", source = "id")`** — da `getId()` direkt das Value Object zurückgibt

#### 5h. Service

- `<Aggregate>Service.java` — `@Service`, `@Validated`, `@RequiredArgsConstructor`, `public class`
- Injiziert Repository + `OutboxEventRepository`
- **Immer `org.springframework.transaction.annotation.Transactional`** verwenden — NICHT `jakarta.transaction.Transactional`
- **Query-Methoden:** geben Views/Optional zurück
- **Command-Methoden:** `@Transactional`, delegieren an Aggregate, persistieren Entity + Outbox-Events
- Creation: `Entity.of(command)` → `save()` → `EventFactory.created(entity)` → `saveOutboxEvents()`
- Mutation: `findOrThrow()` → `entity.command()` → Events → `saveOutboxEvents()`
- Private Helper: `saveOutboxEvents()`, `findOrThrow()`, `throw...Exception()` Methoden
- **Uniqueness-/Existenz-Prüfungen:** mit `Optional.ifPresent(existing -> throwXxxException(..., existing))` +
  dedizierter privater `throw...Exception()`-Methode — keine Inline-Lambdas mit `throw new ...`

#### 5i. Web Layer (`web/`)

- `<Aggregate>ApiModel.java` — MapStruct `@Mapper extends ValueObjectMapper`, `public interface`
    - Response-DTOs: **ID-Felder als `String`** (via `@Mapping(target = "id", source = "id.value")`)
    - Request-DTOs: Verwende Value Objects, `@JsonCreator` deserialisiert automatisch
    - Kein manuelles `UUID → ValueObject` Mapping im Controller nötig
- `<Aggregate>Controller.java` — `@RestController`, `@Validated`, `@RequiredArgsConstructor`
    - Injiziert Use Cases (und Service nur für Query-Endpoints)
    - Query-Endpoints: delegieren an Service
    - **Command-Endpoints: delegieren IMMER an Use Cases** — niemals direkt an den Service
    - Path: `/api/v1/<aggregate-name-kebab-case>`
    - **`@PathVariable` und `@RequestParam` verwenden IMMER Value Objects** (z.B. `@PathVariable RfidReaderId rfidReaderId`)
      — NIEMALS `UUID` oder `String` mit manueller Konvertierung
    - Optionale Filter-Parameter: `@RequestParam(required = false) ValueObject param` + `Objects.nonNull()` Check
    - **Kein Query/Command-Mix:** Ein Endpoint ist entweder Query ODER Command, niemals beides zusammen (kein "get-or-create")

### 6. Use Cases (`use_case/`)

- Eine Klasse pro Use Case — `@Service`, `@Validated`, `@RequiredArgsConstructor`
- **Jede Command/Mutation geht über einen Use Case** — der Controller ruft niemals direkt den Domain-Service auf
- **Immer `org.springframework.transaction.annotation.Transactional`** verwenden — NICHT `jakarta.transaction.Transactional`
- Orchestriert Validierung, Existenzprüfungen und delegiert dann an den Domain-Service
- **Existenzprüfungen** für referenzierte Aggregate (Customer, Tool, etc.) gehören hierher
    - Pattern: `service.get(id).orElseThrow(() -> ResourceNotFoundException.xxx(id))`
    - Bei Listen: `ids.forEach(this::verifyCustomerExists)` — alle Einträge vor der eigentlichen Operation prüfen
- `@Transactional` auf der `process()`-Methode (außer bei Batch mit Teilerfolgen — dort ist jeder Einzel-Aufruf im
  Service eigenständig transaktional)
- Batch-Use-Cases: Teilerfolge mit Result-DTO (`List<Success>` + `List<Error>`)
- **Naming-Konvention:** `<Aggregate><Verb><Object>` (z.B. `RfidReaderCreate`, `RfidReaderUpdate`,
  `RfidReaderDelete`, `RfidReaderChangeType`, `ToolAddInstructors`, `ToolRemoveInstructors`)
- Auch für einfache CRUD-Operationen (Create, Update, Delete) — damit der Controller konsistent bleibt

### 7. DB-Migration

- `V<next>__<aggregate_name>.sql` in `src/main/resources/db/migration/`
- `CREATE TABLE schaffbar.<aggregate_name> (...)` mit PK, Constraints, Indizes
- `GRANT ALL ON TABLE schaffbar.<aggregate_name> TO schadmin;`
- Unique Constraints und Indizes nach Bedarf
- **Keine Foreign Keys:** Gemäß DDD werden keine Foreign Keys auf Datenbankebene zwischen Aggregaten verwendet.
  Referenzielle Integrität wird auf Applikationsebene (Use Cases, Existenzprüfungen) sichergestellt.
  Foreign Keys innerhalb eines Aggregats (z.B. `@ElementCollection`-Tabellen) werden ebenfalls nicht verwendet —
  JPA/Hibernate übernimmt das Lifecycle-Management der zugehörigen Einträge.

### 8. Dokumentation aktualisieren

**Package-README** (primäre Aggregate-Doku):

- `README.md` im neuen Package erstellen mit: Aggregate Root, Identity, Attributes, Commands, Business Rules, Domain
  Events
- Dies ist die **Single Source of Truth** für das Aggregate
- Bei Änderungen am Domain-Modell (neue Business Rules, Commands, Events, Attribute, etc.) immer README file
  aktualisieren
- Vor allem Business Rules immer aktuell halten, da sie die Grundlage für die Implementierung und die Dokumentation der
  Domain-Logik bilden

**Zentrale Docs** (`docs/ddd-overview.md` EN + `docs/ddd-uebersicht.md` DE) an folgenden Stellen ergänzen:

1. **Domain Overview** — neuen Bullet-Point
2. **Aggregate Catalog Tabelle** — neue Zeile mit Link zur Package-README
3. **Use Cases Tabelle** — neue Use Cases + bestehende aktualisieren falls betroffen
4. **Value Objects Tabelle** — neues Value Object
5. **Complete Event List** — neue Events
6. **Appendix TODOs** — erledigte TODOs als resolved markieren, neue hinzufügen falls nötig

### 9. Migration bestehender Aggregate (falls bereits vorhanden)

- Wenn ein bestehendes Aggregate noch `...WithEvents` nutzt:
    - `of(...)` auf direkte Rückgabe der Entity umstellen
    - `...WithEvents`-Record entfernen
    - Creation-Event im Service nach `save(entity)` erzeugen und per Outbox speichern
- Mutations-Methoden im Aggregate bleiben unverändert: Rückgabe `List<SchaffbarEvent>`

## Konventionen-Zusammenfassung

- `Objects.nonNull()` / `Objects.isNull()` statt `!= null` / `== null`
- Value Objects überall in Payloads, Views, ApiModel DTOs, Commands und Request/Response Bodies
- Raw-Typen (`UUID`, `String`) nur intern in Entity-Feldern
- Package-private als Default, `public` nur wo nötig (Service, Views, Commands, Enums, DTOs)
- Events über Transactional Outbox Pattern
- `of()` gibt Entity zurück, Creation-Event im Service
- In `of()`: `<Aggregate>Id.random().getValue()` — NICHT `UUID.randomUUID()`
- Command-Methoden geben `List<SchaffbarEvent>` zurück
- Existenzprüfungen in Use Cases, Zustandsprüfungen im Aggregate
- Keine `IllegalStateException` oder `RuntimeException` — immer domain-spezifische Custom Exceptions definieren
- Listen-Operationen (add/remove mehrere Einträge) bevorzugen `List<ValueObject>` statt einzelne Werte
- **Jede Mutation/Command geht über einen Use Case** — Controller delegiert nie direkt an den Domain-Service
- **Immer `org.springframework.transaction.annotation.Transactional`** — niemals `jakarta.transaction.Transactional`
- **`@Setter(AccessLevel.PRIVATE)`** — niemals `@Setter` ohne Access Level auf Entities
- **`getId()` für eigene Identity** — nicht `get<Aggregate>Id()`
- **`@PathVariable` / `@RequestParam` immer mit Value Objects** — niemals `UUID` / `String` mit manueller Konvertierung
- **ID-Felder in Response-DTOs als `String`** — nicht `UUID`
- **Kein Query/Command-Mix in einem Endpoint** — kein "get-or-create" Pattern
