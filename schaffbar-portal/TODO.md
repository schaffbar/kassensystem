- [Story] Assignment und Unassignment von fixen RFID tags

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

Technical debt:

1. Migrate to Spring Boot 4
2. Facade vs Use Case
3. Transactional annotation in all services
4. Pfad zu Daten in docker-compose file konfigurierbar machen
5. Genauer definieren was erlaubt ist in WaitingForAssignment und was in Assigned status
6. TestController zerschlagen und responses fixen
7. init.sql Add indexes after initial testing phase
8. init.sql Add NOT NULL constraints to customer

Info:

- bei angefangener arbeit mindestens ein Einheit
- Zuweisung von Einweisung zu Tool nicht Tool Grupe
- Unassingment auch ohne token im sonderfall
-

Fragen:

- Jahresabo vom 1.1 oder vom Tag wo es gakauft wurde
