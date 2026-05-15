# Skill: Domain Package erstellen (Schaffbar Core-POS)

## Wann nutzen
Wenn ein neues Domain-Package (Aggregate) im Schaffbar Core-POS Projekt erstellt werden soll – analog zu `tool_certification`, `tool_usage`, `customer`, etc.

## Projekt-Kontext
- **Basis-Package:** `de.schaffbar.core_pos`
- **DB-Schema:** `SCHAFFBAR`
- **Build:** Maven, Java 21+, Spring Boot, JPA/Hibernate, MapStruct, Lombok, Jakarta Validation
- **Architektur:** DDD mit Transactional Outbox Pattern für Domain Events
- **Dokumentation:** `docs/ddd-overview.md` (EN) und `docs/ddd-uebersicht.md` (DE)

## Checkliste – Dateien und Reihenfolge

### 1. Value Object (shared/id)
- `<Aggregate>Id.java` in `shared/id/` — nach dem Muster von `ToolCertificationId` / `ToolUsageId`:
  - `@Value(staticConstructor = "of")`, `@JsonValue`, `@JsonCreator`
  - `random()` Factory-Methode
  - `sameValueAs()` Vergleichsmethode
- `TOOL_CERTIFICATION_ID` o.ä. zum `ValueObjectAssert.ValueObject` Enum hinzufügen

### 2. Converter (shared/util)
- `StringTo<Aggregate>IdConverter.java` — `implements Converter<String, <Aggregate>Id>`, `@Component`
- Notwendig für `@PathVariable` und `@RequestParam` Deserialisierung

### 3. Event-Infrastruktur (shared/event)
- `AggregateType` Enum — neuen Wert hinzufügen
- `EventType` Enum — alle Event-Typen des neuen Aggregats hinzufügen
- `<Aggregate>EventPayload.java` in `shared/event/payload/` — Marker-Interface: `extends EventPayload`
- `<Aggregate>Payloads.java` in `shared/event/payload/` — Payload-Records die `<Aggregate>EventPayload` implementieren
  - **Verwende immer Value Objects** (`CustomerId`, `ToolId`, etc.) für ID-Referenzen, niemals `UUID`/`String`
- `SchaffbarEvent.java` — neue statische Factory-Methode `<aggregate>Event(EventType, <Aggregate>Id, <Aggregate>EventPayload)` hinzufügen + Import für Payload und Id

### 4. Exceptions (shared/exception)
- Domain-spezifische Exceptions nach Bedarf in `shared/exception/`
- Pattern: `extends RuntimeException`, `@Serial`, statische `MESSAGE`-Konstante, Konstruktor mit Value Objects
- `ResourceNotFoundException` — neue Factory-Methode + neuen `Resource` Enum-Wert hinzufügen falls nötig

### 5. Aggregate-Package (`<aggregate_name>/`)

Alle folgenden Dateien sind **package-private** (außer Service, Views, Commands, Status-Enum und BatchResult-DTOs die `public` sind):

#### 5a. Status/Type Enum
- `<Aggregate>Status.java` (falls Lifecycle vorhanden) — `public enum`

#### 5b. Commands
- `<Aggregate>Commands.java` — `public interface` mit `record`-Typen
- Bean Validation Annotationen (`@Valid`, `@NotNull`, etc.)
- Verwende Value Objects als Feld-Typen

#### 5c. Aggregate Root (Entity)
- `<Aggregate>.java` — JPA `@Entity`, `@Table(name = "...", schema = "SCHAFFBAR")`
- Lombok: `@Getter`, `@Setter(AccessLevel.PRIVATE)`, `@NoArgsConstructor(access = AccessLevel.PROTECTED)`, `@ToString`
- Felder intern als Raw-Typen (`UUID`, `String`), Getter geben Value Objects zurück
- `@Id` auf UUID, `@Version` auf `Instant updatedAt`, `@Enumerated(EnumType.STRING)` für Enums
- **Statische Factory `of(CreateCommand)`** gibt **direkt die Entity** zurück (kein `WithEvents`-Record). Das Creation-Event wird im Service erzeugt.
- **Command-Methoden** (`pause()`, `revoke()`, etc.) geben `List<SchaffbarEvent>` zurück
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

#### 5g. Views
- `<Aggregate>Views.java` — MapStruct `@Mapper`, `public interface`
- Singleton: `MAPPER = Mappers.getMapper(...)`
- View-Records mit Value Objects als Feld-Typen

#### 5h. Service
- `<Aggregate>Service.java` — `@Service`, `@Validated`, `@RequiredArgsConstructor`, `public class`
- Injiziert Repository + `OutboxEventRepository`
- **Query-Methoden:** geben Views/Optional zurück
- **Command-Methoden:** `@Transactional`, delegieren an Aggregate, persistieren Entity + Outbox-Events
- Creation: `Entity.of(command)` → `save()` → `EventFactory.created(entity)` → `saveOutboxEvents()`
- Mutation: `findOrThrow()` → `entity.command()` → Events → `saveOutboxEvents()`
- Private Helper: `saveOutboxEvents()`, `findOrThrow()`, Validierungs-Methoden (`ensure...()`)

#### 5i. Web Layer (`web/`)
- `<Aggregate>ApiModel.java` — MapStruct `@Mapper extends ValueObjectMapper`, `public interface`
  - Response-DTOs: Verwende Value Objects, `@JsonValue` serialisiert automatisch
  - Request-DTOs: Verwende Value Objects, `@JsonCreator` deserialisiert automatisch
  - Kein manuelles `UUID → ValueObject` Mapping im Controller nötig
- `<Aggregate>Controller.java` — `@RestController`, `@Validated`, `@RequiredArgsConstructor`
  - Injiziert Service + Use Cases
  - Query-Endpoints: delegieren an Service
  - Command-Endpoints: delegieren an Use Cases
  - Path: `/api/v1/<aggregate-name-kebab-case>`

### 6. Use Cases (`use_case/`)
- Eine Klasse pro Use Case — `@Service`, `@Validated`, `@RequiredArgsConstructor`
- Orchestriert aggregatübergreifende Logik
- **Existenzprüfungen** für referenzierte Aggregate (Customer, Tool, etc.) gehören hierher
  - Pattern: `service.get(id).orElseThrow(() -> ResourceNotFoundException.xxx(id))`
- `@Transactional` auf der `process()`-Methode (außer bei Batch mit Teilerfolgen — dort ist jeder Einzel-Aufruf im Service eigenständig transaktional)
- Batch-Use-Cases: Teilerfolge mit Result-DTO (`List<Success>` + `List<Error>`)

### 7. DB-Migration
- `V<next>__<aggregate_name>.sql` in `src/main/resources/db/migration/`
- `CREATE TABLE schaffbar.<aggregate_name> (...)` mit PK, Constraints, Indizes, FKs
- `GRANT ALL ON TABLE schaffbar.<aggregate_name> TO schadmin;`
- Unique Constraints und Indizes nach Bedarf

### 8. Dokumentation aktualisieren

**Package-README** (primäre Aggregate-Doku):
- `README.md` im neuen Package erstellen mit: Aggregate Root, Identity, Attributes, Commands, Business Rules, Domain Events
- Dies ist die **Single Source of Truth** für das Aggregate

**Zentrale Docs** (`docs/ddd-overview.md` EN + `docs/ddd-uebersicht.md` DE) an folgenden Stellen ergänzen:
1. **Domain Overview** — neuen Bullet-Point
2. **Aggregate Catalog Tabelle** — neue Zeile mit Link zur Package-README
3. **Use Cases Tabelle** — neue Use Cases + bestehende aktualisieren falls betroffen
4. **Value Objects Tabelle** — neues Value Object
5. **Complete Event List** — neue Events
6. **Appendix TODOs** — erledigte TODOs als resolved markieren, neue hinzufügen falls nötig

## Konventionen-Zusammenfassung
- `Objects.nonNull()` / `Objects.isNull()` statt `!= null` / `== null`
- Value Objects überall in Payloads, Views, ApiModel DTOs, Commands und Request/Response Bodies
- Raw-Typen (`UUID`, `String`) nur intern in Entity-Feldern
- Package-private als Default, `public` nur wo nötig (Service, Views, Commands, Enums, DTOs)
- Events über Transactional Outbox Pattern
- `of()` gibt Entity zurück, Creation-Event im Service
- Command-Methoden geben `List<SchaffbarEvent>` zurück
- Existenzprüfungen in Use Cases, Zustandsprüfungen im Aggregate
