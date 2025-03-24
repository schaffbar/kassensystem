# kassensystem
## Projektbeschreibung
Die ist die Geschichte für die Entwicklung eines Abrechnungssystems für die SchaffBar,
einer offenen Werkstatt. Es sollen die Anwesenheiten der Besucher als die Nutzungszeiten
für ausgewählte elektrische Geräte zu Abrechnungszwecken erfasst werden.

Mittels eines RFID-Tags, das dem Besucher ausgehändigt wird, bei der Registrierung an der Theke,
wird beim Betreten und Verlassen des Werkstattbereichs ein Zeitpaar bestimmt, 
das zur Abrechnung im Kassensystem herangezogen wird. 
An einem Gerät kann mittels des RFID-Tags und einem WLan-Relais die Versorgungsspannung 
eines Gerätes ein und ausgeschlatet werden und damit ebenfalls ein weiteres Zeitpaar 
erstellt werden, das zur Abrechnung verwendet werden kann.

Das Lesen der RFID-Tags kann mittels eines RFID-Lesers erfolgen,
während die Daten in einer Datenbank auf einem Server gespeichert werden.

Daraus ergibt sich für das System folgende Aufteilung
-)rfidReader - Bereitstellung des RFID-Tags an unterschiedlichen Positionen
-)TimeMachine - Software auf dem Server dient als Schnittstelle zum Datenbankystem
-)ein Kassensystem

Als Kassensystem wird aktuell mit einer Offline Version von Odoo gearbeitet,
diese läuft in einem Docker-Container, in einem weiteren Container befindet sich die Datenbank,
ein weiteren Container wird es für die TimeMachine-Software geben.


##rfidReader
An folgenden oben beschriebenen Position kann ein RFID-Reader zur Anwendung kommen.
-) dem Tresen
-) dem Eingangsbereich zur Werkstatt
-) an einem elektrischem Gerät "Gerätekontrolle"
diese Anwendungsfälle werden im weiteren Verlauf auch UseCases genannt.

Aktuell wird die ArduinoIDE zur Entwicklung verwendet, um aber die verwendeten Bibliotheken mitzuliefern 
soll die Entwicklung auf PlatformIO umgestellt werden.
Auch soll die LVGL Bibliothek zukünftig verwendet werden, damit später das Handling von Schaltflächen und
anderem leichter implementiert werden kann.

### Hardwarebeschreibung
Eine Mikroprozessorsystem soll folgende Hardwaremerkmale aufweisen.
-) ein Mikroprozessor mit einer WLan-Schnittstelle zur Kommunikation mit dem Server und den Datenbanken, 
-) einem RFID-Modul zum Lesen der ID-Nummer 
-) ein Display zur Darstellung von Informationen an den Benutzer,
   beziehungsweise Interaktion über die Touch-Funktionalität. 

### Softwarebeschreibung
### Konfigurationsdaten
Um den Konfigurationsaufwand gering zuhalten wird beim Start des RFID-Readers die MAC-Adresse an den Server gesendet,
und anhand der Einträge in der Datenbank werden die Konfigurationsdaten an den RFID-Reader übertragen.

#### Tresen  
Nach dem Auflegen des RFID-Tags auf dem RFID-Reader wird diese an den Server übertragen und in einer Datensatz 
der Datenbank abgelegt und steht zur weiteren Verarbeitung am Tresen zur Verfügung, wie Bearbeitung der Besucherdaten,
zur Abrechnung, oder ...
Am Tresen erfolgt die Zuordnung des RFID-Tags zu einem Besucher

#### Eingangsbereich
Durch die Registrierung des Besuchers an der Theke hat dieser einen RFID-Tag erhalten, im Anschluss kann er seine 
Sachen richten, die er mit in den Werkstattbereich nehmen möchte. Beim Betreten und Verlassen des Werkstattbereichs
hält der Besucher sein RFID-Tag an den RFID-Leser zur Erstellung einer Zeitmarke, die Differenz der Zeitspanne 
entspricht der Nutzungsdauer zur Abrechung. In Abhängigkeit der vorhandenen Datenbankeinträge ergibt sich das Betreten
bzw. Verlassen der Werkstatt. Zur Berücksichtigung von Pausen oder ähnlichem können in der Datenbank mehrere Zeitpaare
eines Besuchers angelegt werden.

#### Gerätekontrolle
In den Stammdaten eines Besuchers ist hinterlegt, ob für einen Kunden die jeweilige Unterweisung für das Gerät erfolgt ist,
damit kann die unberechtige Verwendung von Geräten verhindert und die Unfallgefahr reduziert werden.
Nur wenn die Unterweisung hinterlegt ist, darf die Versorgungsspannung eingeschaltet werden, auch hier erfolgt die Abfrage
über die die Software auf dem Server. 

## TimeMachine
Diese Software bildet das Bindeglied zwischen den RFID-Reader und der Datenbank,
in Abhängigkeit des UseCases und dem Ergebnis der Datenbankabfrage wird die entsprechende Ausgabe auf dem RFID-Reader gessteuert.

##Kassensystem
Aktuell soll eine offline Version von Odoo als Kassensystem verwendet werden,
da hier viele Funktionen bereits vorhanden sind. 
Die Kopplung der RFID-Reader an das Kassensystem sowie die Auswertung der zusätzlichen Informationen 
aus den hinzugefügten Tabellen muss noch erfolgen.

