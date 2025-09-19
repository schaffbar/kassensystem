## Vorbedingung

- RFID-Tag kann nur einem Benutzer zugewiesen werden, der allgemeine Einweisung absolviert hat.
- RFID-Tag und RFID-Reader müssen beide bekannt sein.
- RFID-Tag muss einem User zugewiesen sein.

## Nutzung vom Werkstatt

- Ein Benutzer kann an einem Tag mehrmals zum Schaffbar kommen.
- Ein Benutzer kann innerhalb eines Aufenthalts mehrmals Werkstatt betretten.
- Nur Benutzer mit einem bekannten RFID-Tag kann Werkstatt nutzen.

### Start

-

### Stop

-

## Nutzung von Maschinen

- Ein Benutzer kann höchstens x Maschinen gleichzeitig nutzen.
- Eine Maschine kann nur von einem Benutzer benutzt werden.
- Bevor ein Benutzer eine Maschine benutzen kann, muss er

  - muss er beim Torwächter stempeln
  - gerätspezifische Einweisung absolvieren

- Eine Maschine muss WLAN-Relais Informationen haben, bevor sie benutzt werden kann.
- Eine Maschine muss mit einem RFID-Reader verbunden sein

### Start

-

### Stop

-

## Abrechnung

- Bevor der Benutzer Schaffbar verlässt, muss er abgerechnet werden. Wenn nicht, was dann ...

...

- Nach erfolgreicher Abrechnung speichere die Session in History

###############################################################################

RFID-Reader

- toolId

RFID-Tag

- id

User

- allgemeine einweisung

Tool

- wlan relais configuration

##########

Absolvierte Einveisungen

- userId
- einweisungId ???
- toolId ???

Einweisung

- toolId (multiple toolIds)

###############################################################################

Fragen:

- Was passiert, wenn der Kunde Werkstatt verlässt aber die Maschinen hat er nicht gestopt? Unterschiedung zwischen Kreissäge und 3D-Drucker.
- Wenn wir mehrere Kreissägen vom unterschiedlichen Typ haben, brauchen wir eine oder mehrere Einweisungen?
- Wenn die Abrechnung nicht am gleichen Tag passiert, was machen wir mit dem User / RFID-Tag? (nichts/sperren)
