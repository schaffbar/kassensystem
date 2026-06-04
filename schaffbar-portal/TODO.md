- [Backend] Scheduler to clean up waiting for assignment
- [Backend] put bruno collection to backend
- [Backend] fix/implement/remove TODOs

- [Technical] upgrade Angular version
- [Technical] Migrate to Spring Boot 4
- [Technical] open telemetry

- [Backend] Unit tests
- [Frontend] Fix Geburtsdatum
- [Frontend] users and tools store should be global?
- [Frontend] dashboard store - withEntities vs withState

#######################################

- Nutzt jakarta.transaction.Transactional statt org.springframework.transaction.annotation.Transactional

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
- [Backend] Enter and leave time as LocalDateTime instead of Instant?
- [Backend] Problems API
- [Backend] Beim Löschen immer überprüfen ob Entity nicht irgendwo referenziert wird
- [Backend] Check if workshop usage is completed before closing session
- [Backend] Validate Session before closing it

- [Technical]
- [Technical] Facade vs Use Case
- [Technical] Move all mutations to use cases
- [Technical] Transactional annotation in all services
- [Technical] Pfad zu Daten in docker-compose file konfigurierbar machen
- [Technical] Genauer definieren was erlaubt ist in WaitingForAssignment und was in Assigned status
- [Technical] DeviceController zerschlagen und responses fixen
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

#####################

**Steps**

### Phase 1: Temporären Clone vorbereiten

1. `kassensystem` in ein temporäres Verzeichnis klonen (Branch `piotr`):

   ```
   git clone --branch piotr /Users/krantzp/source/private/kassensystem /tmp/kassensystem-migrate
   ```

2. `git-filter-repo` installieren (falls nicht vorhanden):
   ```
   brew install git-filter-repo
   ```

### Phase 2: History filtern

3. Nur die gewünschten Pfade behalten — alle anderen Commits/Dateien werden entfernt:
   ```
   cd /tmp/kassensystem-migrate
   git filter-repo \
     --path schaffbar-backend/ \
     --path schaffbar-portal/ \
     --path deployment.md \
     --path interfaces.md \
     --path use-cases.md \
     --path workshop-usage.md
   ```
   Commits, die _nur_ Hardware-Dateien berührt haben, werden automatisch gedroppt.

### Phase 3: In kassensystem-software mergen

4. Den gefilterten Clone als Remote hinzufügen:

   ```
   cd /Users/krantzp/source/private/kassensystem-software
   git remote add migration /tmp/kassensystem-migrate
   git fetch migration
   ```

5. Merge mit `--allow-unrelated-histories` (da unterschiedliche Repo-Wurzeln):

   ```
   git merge migration/piotr --allow-unrelated-histories
   ```

6. Temporären Remote entfernen:
   ```
   git remote remove migration
   ```

### Phase 4: Aufräumen

7. Temporären Clone löschen: `rm -rf /tmp/kassensystem-migrate`
8. Push: `git push origin main`

---

**Verification**

1. Nach filter-repo: `git log --oneline` im Temp-Clone → nur relevante Commits vorhanden
2. Nach Merge: `git log --oneline` in kassensystem-software → History ist vollständig
3. `ls` zeigt schaffbar-backend, schaffbar-portal und die Doku-Dateien
4. Keine Hardware-Dateien vorhanden: `find . -path './platine*' -o -path './ArduinoIDE*'` → leer

---

**Decisions**

- timeMachine wird **nicht** migriert
- Commits die ausschließlich ausgeschlossene Dateien betrafen, fallen weg (gewollt)
- Original-Repo `kassensystem` bleibt vollständig unverändert
- Verzeichnisstruktur bleibt identisch

---

Soll ich den Plan so umsetzen, oder gibt es Anpassungen?

########################

Ja, das ist möglich. Der einfachste Weg für inkrementelle Updates ist `git format-patch` + `git am`:

**In kassensystem** (Patches für neue Commits erzeugen):

```bash
cd /Users/krantzp/source/private/kassensystem
git format-patch 905a7ae4..HEAD -- schaffbar-backend/ schaffbar-portal/ deployment.md interfaces.md use-cases.md workshop-usage.md -o /tmp/patches
```

`905a7ae4` war der letzte Commit im piotr-Branch zum Zeitpunkt der Migration. Alles danach sind neue Commits.

**In kassensystem-software** (Patches anwenden):

```bash
cd /Users/krantzp/source/private/kassensystem-software
git am /tmp/patches/*.patch
```

Das überträgt jeden neuen Commit (mit Autor, Datum, Message) einzeln — aber nur wenn er die relevanten Pfade berührt hat. Commits die nur Hardware-Dateien betreffen erzeugen leere Patches und können übersprungen werden (`git am --skip`).

**Wichtig:** Merk dir `905a7ae4` als "letzte migrierte Commit-ID". Nach der nächsten Sync-Runde aktualisierst du den Marker auf den neuen HEAD.
