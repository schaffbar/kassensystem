# Setup
## Arduino IDE
- Folge dieser Anleitung um die ESP Platform in der Arduino IDE zu aktivieren: https://docs.espressif.com/projects/arduino-esp32/en/latest/installing.html
- Installiere die fehlenden Bibliotheken.
- Board: ESP32C3
- USB CDC On Boot: Enabled
  (ansonsten funktioniert die Ausgabe der seriellen Schnittstelle nicht.)


## Visial Studio Code
- Im Visual Studio das PlatformIO plugin installieren
- platform.ini muß folgende Zeile enthalten, sonst funktioniert die serielle Konsole nicht.
```
build_flags = -DCORE_DEBUG_LEVEL=0 -DARDUINO_USB_MODE -DARDUINO_USB_CDC_ON_BOOT=1
```
- Installiere die fehlenden Bibliotheken.


