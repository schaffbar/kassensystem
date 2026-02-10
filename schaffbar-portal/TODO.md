TODO:

1. Setzen von RFID reader type in UI
2. Scheduler to clean up waiting for assignment
3. Check if workshop usage is completed before closing session
4. Validate Session before closing it
5. Löschen von Entitäten immer bestätigen
6. ========================================================
7. Beim Löschen immer überprüfen ob Entity nicht irgendwo referenziert wird
8. Assignment und Unassignment von fixen RFID tags
9. Einweisung plus Zuweisung zu User
10. Switch box implementieren

11. Nutzung von Maschinen in minuten oder stunden
12. Zuweisung von RFID reader zu tool
13. Dashboard für aktive user, aktive tools, ...
14. Auschalten von Maschinen, wenn user Raum verlässt

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
