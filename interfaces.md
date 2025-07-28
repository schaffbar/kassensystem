# Ideen:

- Nutze so was wie Session (eine pro Abrechnung), WerkstattSession und WerkzeugSession für die Abrechnung.
- Prüfe ob der Kunde WerkstattSession offen hat bevor die WerkzeugSession starten und Werkzeug freigeschaltet wird
- Fallback wenn RFID-Reader funktioniert nicht. Vom Backend direkt auf WLAN-Relais gehen?
- Aktiviere RFID-Reader per UI/Portal. Wieso?
- Synchronize Backend und RFID-Reader (alle x Minuten)
  - state (im Falle von SwitchBox), könnte wichtig beim RFID-Absturz sein
  - type
- In Portal die Zuweisunng vom RFID-Reader zum Typ machen und validieren, dass
  - RFID-Reader nur einen Typ haben kann
  - es nur ein Counter und nur ein GateKeeper sein kann
  - Typ Switchbox nur einem Werkzeug zugewiesen kann und das Werkzeug nur ein RFID-Reader haben kann

# REST

## /register

- [GET] Hole RFID-Tag Id vom Clipboard
- [UI] Anmelden button
- [POST] Zuweisung vom RFID-Tag zum Kunden (Speichert nur Name von form)

## /lookup

- [GET] Hole RFID-Tag Id vom Clipboard
- [UI] Suche button
- [POST] weiterleiten an checkout interface

## /checkout/<rfid>

- [GET] Hole Abrechnung
- [UI] Als bezahlt markieren
- [POST] markiere CardActions als abgerechnet

# RFID_READER

## Allgemeine Fragen:

- So wie ich es sehe ist RFID-Tag nicht am Kunden angebunden, sondern er speichert nur Name. Ist das korrekt?
- Wie unterscheidet RFID-Reader welchen Request er verschickt? Anhand vom Typ?
- Welche usecases/RFID-Reader types gibt es?

  - S -> SwitchBox
  - C -> Counter ??? Theke ???
  - A -> ???
  - G -> GateKeeper ???
  - "" -> wenn RFID-Reader started und noch keine Konfiguration vorhanden ist

- Wie wird RFID-Reader Typ gesetzt? Manuell?
- Wie passeirt die Zuweisung zum Werkzeug? Manuell?
- Über welche Schnittstelle bekommt der RFID-Reader sein Typ. Ich gehe davon aus, dass er kurz nach dem Start noch sein Typ nicht kennt und ihn von Backend bekommen muss. Oder ist das falsch?
- Wie werden die Einweisung eingetragen? Manuell?
- Wer entscheidet in welchem Zustand ist ein Werkzeug? Backend oder RFID-Reader?
- Wieso request enthalten Daten die wir im Backend nicht nutzen?
- Ist state in DeviceInitRequest/Response und in DeviceCardRequest/Response gleich?
- Was ist mit der Überprüfung, wie viele Werkzeuge darf der Kunde nutzen?

## Überblick 'usecase':

### empty:

- DeviceInitRequest: registriere RFID-Reader

### SwitchBox (S):

- DeviceInitRequest: ziehe die WLAN-Relais Konfiguration vom Backend
- DeviceCardRequest: Ein-/Aus-Schalten von Werkzeugen

### GateKeeper (G):

- DeviceCardRequest: Erfassung der Anwesenheitszeit des Kunden in der Werkstatt

### Counter (C):

- CounterCardRequest: setze den RFID-Tag in den Clipboard

### ??? (A):

- CounterCardRequest: registriere RFID-Tag wenn noch nicht existiert

## DeviceInitRequest

```python
class DeviceInitRequest(BaseModel):
MACADDR: str
STATE: str
DEVUSECASE: str
```

```python
class DeviceResponse(BaseModel):
    DATEY: int = 0
    DATEM: int = 0
    DATED: int = 0
    TIMEH: int = 0
    TIMEM: int = 0
    TIMES: int = 0

    def __init__(self, **data):
        # Call the parent constructor to handle field validation and assignment
        super().__init__(**data)
        # Set the `now` field to the current datetime
        now = datetime.now()
        self.DATEY = now.year
        self.DATEM = now.month
        self.DATED = now.day
        self.TIMEH = now.hour
        self.TIMEM = now.minute
        self.TIMES = now.second

class DeviceInitResponse(DeviceResponse):
    STATE: str
    DEVNAME: str
    DEVUSECASE: str
    STARTHTTP: str
    DEVIP: str
    SWITCHON: str
    SWITCHOFF: str
    TERMINAL: str
    ERROR: str
```

### Registrieren RFID-Reader

- wenn die im Request gegebene MAC Adresse nicht existiert in der Datenbank
- erstelle RFID-Reader Instanz mit gegebene MAC Adresse

### Hochfahren von RFID-Reader (im Kommentar steht Ein-/Aus-Schalten von Werkzeugen, ist das falsch?)

- wenn die im Request gegebene MAC Adresse existiert in der Datenbank
- und der Typ von RFID-Reader ist SwitchBox
- und Werkzeug Id ist gesetzt und vorhanden in der Datenbank
- dann setzte die WLAN-Relay Konfiguration in Response

### Fragen:

- Wozu im request STATE und DEVUSECASE wenn nur MAC-Adresse genutzt wird?
- Wo wird beim Einschalten die Einweisung überprüft?

## CounterCardRequest

```python
class CounterCardRequest(BaseModel):
    MACADDR: str
    STATE: str
    DEVUSECASE: str
    RFID: str
    TERMINAL: str
    ICON: str
    ERROR: str
```

```python
class CounterCardResponse(BaseModel):
    DEVUSECASE: str
    ERROR: str
    STATE: str
    ICON: str
```

### Setzte RFID-Reader Id zu Clipboard

- wenn der Typ von RFID-Reader ist Counter
- und RFID-Tag existiert in der Datenbank

### Füge neues RFID-Tag hinzu

- wenn der Typ von RFID-Reader ist A ???
- und RFID-Tag existiert nicht in der Datenbank
- ansonsten return RFID-Tag bereits vorhanden

### Fragen:

- Wieso wird ein RFID-Tag hinzugefügt in die Datenbank. Jeder kann doch kommen mit eigenem RFID-Tag

## DeviceCardRequest

```python
class DeviceCardRequest(BaseModel):
    MACADDR: str
    STATE: str
    DEVUSECASE: str
    RFID: str
    ICON: str
    ERROR: str
    CUSTOMERNAME: str
    CUSTOMERSTARTSTOP: str
```

```python
class DeviceCardResponse(BaseModel):
    CUSTOMERNAME: str
    CUSTOMERSTARTSTOP: str
    ICON: str
    STATE: str
    UNITS: str
    ERROR: str
    DEVUSECASE: str
```

- get RFID-Reader by MAC-Adresse
- get RFID-Tag by rfid (was ist das? RFID-Reader Id vs RFID-Tag Id)

- wenn RFID-Tag existiert nicht in der Datenbank -> ERROR

- wenn RFID-Reader existiert in der Datenbank (und auch RFID-Tag)
- hole CardActions für gegebenen RFID-Tag und RFID-Reader, die noch nicht abgerechnet wurden
- hole Kunden name, der zu dem RFID-Tag zugewiesen ist

### Erfassung der Anwesenheitszeit des Kunden

- wenn der Typ von RFID-Reader ist GateKeeper
- wenn es keine CardActions gibt oder letzte CardAction is 'checkout' -> new CardAction vom Typ 'checkin'
- ansonsten -> new CardAction vom Typ 'checkout'

#### Fragen:

- Wieso wird Abrechnung gemacht beim 'checkin' und an RFID-Reader als DeviceCardResponse.UNITS gegeben

### Ein-/Aus-Schalten von Werkzeugen

- wenn der Typ von RFID-Reader ist SwitchBox
- prüfe ob der Kunde die Einweisung für dieses Werkzeug gemacht hat
- wenn DeviceCardRequest.STATE == "Idle" erstelle 'checkin' in CardActions und wenn letzte CardAction auch 'checkin' war erstelle davor noch künstliches 'chekcout'
- wenn DeviceCardRequest.STATE nicht "Idle" ist (also wahrschienlich "Working") erstelle 'checkout' in CardActions und wenn letzte CardAction auch 'checkout' war erstelle davor noch künstliches 'chekin'

#### Fragen:

- Wieso wird zweites Mal Kundenname geholt und geprüft?
- Was ist DeviceCardRequest.STATE == "Idle" / "Working"? Wie wird es beim Neustart gesetzt?
- Die kuünstlichen CardActions welchen Zeitstempel sie haben?

# Glossar

## Kunde (Customer)

## (Abrechenbares) Werkzeug (tool) - Vielleicht (Abrechenbare) Maschine

## WLAN-Relay (Shelly)

## RFID-Reader (RFID Lesegerät / device)

- eindeutig identifizierbar durch MAC-Adresse

## RFID-Tag (Token, Card)

- eindeutig identifizierbar durch Id gespeichert auf/in dem Tag

## ??? (CardActions) Zeitstempel

## Einweisung (Zertifikat / Certificate)

# Events

- Werkzeug erstellt
- WLAN-Relais Konfiguration hinzugefügt
-

- RFID-Reader WLAN-Relais Konfiguration gezogen
- RFID-Reader registriert
- Typ von RFID-Reader gesetzt
- Werkzeug zugewiesen
- RFID-Reader aktiviert
-
