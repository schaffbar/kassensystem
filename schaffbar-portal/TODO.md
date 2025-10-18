Done:

- Unassignment von temporären RFID tags und history
- Beim counter name anzeigen, wenn dieser tag zugewiesen ist

TODO:

1. Timo reden wegen Felder, Jugendlichen, Begleitperson, Jahreskarte, ... das ganze bezahlen

- Mitglied
- Geburtsdatum
- Telefonnummer optional
- Booble mit unter 16 und unter 18
- bei angefangener arbeit mindestens ein Einheit

2. Editieren von Usern
3. Setzen von RFID reader type in UI
4. Abrechnung, Schliessen von open session und history
5. Löschen von Entitäten immer bestätigen
6. Scheduler to clean up waiting for assignment
7. ========================================================
8. Beim Löschen immer überprüfen ob Entity nicht irgendwo referenziert wird
9. Setzen von RFID reader type (plus Name/Beschreibung)
10. Assignment und Unassignment von fixen RFID tags
11. Einweisung plus Zuweisung zu User
12. Switch box implementieren

13. Nutzung von Maschinen in minuten oder stunden
14. Beschreibung und Typ von RFID reader
15. Zuweisung von RFID reader zu tool
16. Dashboard für aktive user, aktive tools, ...
17. Auschalten von Maschinen, wenn user Raum verlässt

Technical debt:

1. Transactional annotation in all services
2. Pfad zu Daten in docker-compose file konfigurierbar machen
3. Genauer definieren was erlaubt ist in WaitingForAssignment und was in Assigned status
4. TestController zerschlagen und responses fixen
5. init.sql Add indexes after initial testing phase
6. init.sql Add NOT NULL constraints to customer

INFO:

- Zuweisung von Einweisung zu Tool nicht Tool Grupe
- Unassingment auch ohne token im sonderfall
-

Fragen:

- Jahresabo vom 1.1 oder vom Tag wo es gakauft wurde
