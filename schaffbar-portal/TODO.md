- when changing type of rfid reader from switch box to other, check if this rfid reader is already assigned to a tool. if yes reject request
- check required properties in all entities (update)
- add refres button in user list and tool list
- check all refresh buttons
- always confirm before deleting entity
- tool detail page: sort out rfid readers which are not switchbox
- implement add tool button
- rfid tags list - change coursor to normal when hover item

TODO:

2. Scheduler to clean up waiting for assignment
3. Check if workshop usage is completed before closing session
4. Validate Session before closing it
5. ========================================================
6. Beim Löschen immer überprüfen ob Entity nicht irgendwo referenziert wird
7. Assignment und Unassignment von fixen RFID tags
8. Einweisung plus Zuweisung zu User
9. Switch box implementieren

10. Nutzung von Maschinen in minuten oder stunden
11. Zuweisung von RFID reader zu tool
12. Dashboard für aktive user, aktive tools, ...
13. Auschalten von Maschinen, wenn user Raum verlässt

Technical debt:

1. Transactional annotation in all services
2. Pfad zu Daten in docker-compose file konfigurierbar machen
3. Genauer definieren was erlaubt ist in WaitingForAssignment und was in Assigned status
4. TestController zerschlagen und responses fixen
5. init.sql Add indexes after initial testing phase
6. init.sql Add NOT NULL constraints to customer
7. Enter and leave time as LocalDateTime instead of Instant?

INFO:

- bei angefangener arbeit mindestens ein Einheit
- Zuweisung von Einweisung zu Tool nicht Tool Grupe
- Unassingment auch ohne token im sonderfall
-

Fragen:

- Jahresabo vom 1.1 oder vom Tag wo es gakauft wurde
