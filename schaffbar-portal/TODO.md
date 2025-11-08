Done:

- Unassignment von temporären RFID tags und history
- Beim counter name anzeigen, wenn dieser tag zugewiesen ist
- Timo reden wegen Felder, Jugendlichen, Begleitperson, Jahreskarte, ... das ganze bezahlen
- Editieren von Usern
- Geburtsdatum hinzufügen
- Telefonnummer optional
- Chip mit unter 16 und unter 18
- Mitglied

TODO:

1. ========================================================
2. Abrechnung, Schliessen von open session und history
3. Calculate and show units for open session (do it in backend)
4. Check if workshop usage is completed before closing session
5. When user refreshing, fetch session information
6. Validate Session before closing it
7. Scheduler to clean up waiting for assignment
8. Setzen von RFID reader type und Beschreibung in UI
9. Löschen von Entitäten immer bestätigen
10. ========================================================
11. Beim Löschen immer überprüfen ob Entity nicht irgendwo referenziert wird
12. Assignment und Unassignment von fixen RFID tags
13. Einweisung plus Zuweisung zu User
14. Switch box implementieren

15. Nutzung von Maschinen in minuten oder stunden
16. Zuweisung von RFID reader zu tool
17. Dashboard für aktive user, aktive tools, ...
18. Auschalten von Maschinen, wenn user Raum verlässt

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
