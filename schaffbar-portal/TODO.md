- [Backend] [bug] when changing type of rfid reader from switch box to other, check if this rfid reader is already assigned to a tool. if yes reject request

- [Frontend]
- [Frontend] Select/highligt menu entry when going into the details
- [Frontend] IMPORTANT tools store is used in tool list and tool details. Is it the same instance?
- [Frontend] check required properties in all entities and forms (update)
- [Frontend] handle API errors
- [Frontend] Action feedback - Snackbar?

- [Backend]
- [Backend] Enter and leave time as LocalDateTime instead of Instant?
- [Backend] Scheduler to clean up waiting for assignment
- [Backend] Problems API
- [Backend] validate all events before storing in outbox table

TODO:

3. Check if workshop usage is completed before closing session
4. Validate Session before closing it
5. ========================================================
6. Beim Löschen immer überprüfen ob Entity nicht irgendwo referenziert wird
7. Assignment und Unassignment von fixen RFID tags
8. Einweisung plus Zuweisung zu User
9. Switch box implementieren
10. Nutzung von Maschinen in minuten oder stunden
11. Auschalten von Maschinen, wenn user Raum verlässt

Technical debt:

1. Migrate to Spring Boot 4
2. Transactional annotation in all services
3. Pfad zu Daten in docker-compose file konfigurierbar machen
4. Genauer definieren was erlaubt ist in WaitingForAssignment und was in Assigned status
5. TestController zerschlagen und responses fixen
6. init.sql Add indexes after initial testing phase
7. init.sql Add NOT NULL constraints to customer

Info:

- bei angefangener arbeit mindestens ein Einheit
- Zuweisung von Einweisung zu Tool nicht Tool Grupe
- Unassingment auch ohne token im sonderfall
-

Fragen:

- Jahresabo vom 1.1 oder vom Tag wo es gakauft wurde
