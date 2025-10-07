1. Timo reden wegen Felder, Jugendlichen, Begleitperson, Jahreskarte, ... das ganze bezahlen

- Mitglied
- Jahresabo vom 1.1 oder vom Tag wo es gakauft wurde
- Summieren und durch Einheit teilen oder jeder slot durch Einheit teilen
- 30s ist das schon eine Minute oder eine Einheit

2. Unassignment von temporären RFID tags und history
3. Editieren von Usern
4. Abrechnung, Schliessen von open session und history
5. Beim counter name anzegen, wenn dieser tag zugewiesen ist
6. Löschen von Entitäten immer bestätigen
7. Scheduler to clean up waiting for assignment
8. Genauer definieren was erlaubt ist in WaitingForAssignment und was in Assigned status
9. ========================================================
10. Beim Löschen immer überprüfen ob Entity nicht irgendwo referenziert wird
11. Setzen von RFID reader type (plus Name/Beschreibung)
12. Assignment und Unassignment von fixen RFID tags
13. Einweisung plus Zuweisung zu User
14. Switch box implementieren

15. Nutzung von Maschinen in minuten oder stunden
16. Beschreibung und Typ von RFID reader
17. Zuweisung von RFID reader zu tool
18. Dashboard für aktive user, aktive tools, ...
19. Auschalten von Maschinen, wenn user Raum verlässt

Technical debt:

1. Pfad zu Daten in docker-compose file konfigurierbar machen
2. TestController zerschlagen und responses fixen

INFO:

- Zuweisung von Einweisung zu Tool nicht Tool Grupe
- Unassingment auch ohne token im sonderfall
-
