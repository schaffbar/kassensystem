Done:

- Unassignment von temporären RFID tags und history

TODO:

1. Timo reden wegen Felder, Jugendlichen, Begleitperson, Jahreskarte, ... das ganze bezahlen

- Mitglied
- Jahresabo vom 1.1 oder vom Tag wo es gakauft wurde
- Summieren und durch Einheit teilen oder jeder slot durch Einheit teilen
- 30s ist das schon eine Minute oder eine Einheit

2. Beim counter name anzeigen, wenn dieser tag zugewiesen ist
3. Editieren von Usern
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

1. Pfad zu Daten in docker-compose file konfigurierbar machen
2. Genauer definieren was erlaubt ist in WaitingForAssignment und was in Assigned status
3. TestController zerschlagen und responses fixen

INFO:

- Zuweisung von Einweisung zu Tool nicht Tool Grupe
- Unassingment auch ohne token im sonderfall
-
