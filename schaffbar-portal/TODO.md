- [Story] Save list of Einweiser for each tool in Details Page
- [Story] Add flag for Tool - Need Tool Certification and check it during StartToolUsage use case
- [Frontend] Tool Detail Page - RFID reader and relay zu Configuration mergen
- [Frontend] Remove RFID-Reader input field from Create Tool dialog
- [Frontend] users and tools store should be global?
- [Frontend] dashboard store - withEntities vs withState
- [Backend] Merge dashboard packages (tool_dashboard, workshop_dashboard)
- [Backend] fix/implement/remove TODOs
- [Frontend] Fix Geburtsdatum
- [Technical] Migrate to Spring Boot 4
- [Technical] upgrade Angular version

#######################################

Der gesamte Ausdruck http://<IP-Adresse>/relay/0 ist eine URL (Uniform Resource Locator).
Wenn du den Ausdruck jedoch in seine einzelnen Bestandteile zerlegst, haben die verschiedenen Abschnitte eigene Namen:

- http:// = Das Schema / Protokoll
- <IP-Adresse> = Der Host (oder die Domain)
- /relay/0 = Der Path (Pfad)

#######################################

- [Frontend]
- [Frontend] Select/highligt menu entry when going into the details
- [Frontend] IMPORTANT tools store is used in tool list and tool details. Is it the same instance?
- [Frontend] check required properties in all entities and forms (update)
- [Frontend] handle API errors
- [Frontend] Action feedback - Snackbar?

- [Backend]
- [Backend] Scheduler to clean up waiting for assignment
- [Backend] put bruno collection to backend
- [Backend] Remove CustomerWithEvents Object in all entities
- [Backend] add domain to packages
- [Backend] Enter and leave time as LocalDateTime instead of Instant?
- [Backend] Problems API
- [Backend] validate all events before storing in outbox table
- [Backend] Beim Löschen immer überprüfen ob Entity nicht irgendwo referenziert wird
- [Backend] Check if workshop usage is completed before closing session
- [Backend] Validate Session before closing it

- [Technical]
- [Technical] Facade vs Use Case
- [Technical] Move all mutations to use cases
- [Technical] Transactional annotation in all services
- [Technical] Pfad zu Daten in docker-compose file konfigurierbar machen
- [Technical] Genauer definieren was erlaubt ist in WaitingForAssignment und was in Assigned status
- [Technical] TestController zerschlagen und responses fixen
- [Technical] init.sql Add indexes after initial testing phase
- [Technical] init.sql Add NOT NULL constraints to customer

- [Story]
- [Story] Assignment und Unassignment von fixen RFID tags

- [Docu]
- [Docu] bei angefangener arbeit mindestens ein Einheit
- [Docu] Zuweisung von Einweisung zu Tool nicht Tool Grupe
- [Docu] Unassingment auch ohne token im sonderfall

- [Question]
- [Question] Jahresabo vom 1.1 oder vom Tag wo es gakauft wurde
