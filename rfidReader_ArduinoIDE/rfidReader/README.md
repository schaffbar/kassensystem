# Setup

## Arduino IDE

- Folge dieser Anleitung um die ESP Platform in der Arduino IDE zu aktivieren: https://docs.espressif.com/projects/arduino-esp32/en/latest/installing.html
- Installiere die fehlenden Bibliotheken.
- Tools - Board - esp32 - ESP32C§ Dev Module
- Tools - USB CDC On Boot - Enabled
- anstecken
- Tools - Port - com port mit esp auswählen

## Visial Studio Code

- Im Visual Studio das PlatformIO plugin installieren
- platform.ini muß folgende Zeile enthalten, sonst funktioniert die serielle Konsole nicht.

```
build_flags = -DCORE_DEBUG_LEVEL=0 -DARDUINO_USB_MODE -DARDUINO_USB_CDC_ON_BOOT=1
```

- Installiere die fehlenden Bibliotheken.
