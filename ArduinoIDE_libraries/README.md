# Compilation der rfidReader SW mit der Arduino IDE

Hier werden einige Schritte genannt um die Software der rfidReader mittels der Arduino IDE 
zu übersetzen und auf den Prozessor zu flashen

## Installation der Arduino IDE

Die Ardunio IDE kann unter dem genannten Link für veschiedene Betriebssystem heruntergeladen werden.
https://www.arduino.cc/en/software/

## Verwendung des ESP32 in der Arduino IDE

Um ESP32 Prozessoren in der Arduino ESP nutzen zu können, sind die Schritte von einer der 
Anleitungen hinter den folgenden Links abzuarbeiten.

https://randomnerdtutorials.com/installing-esp32-arduino-ide-2-0/

https://randomnerdtutorials.com/installing-the-esp32-board-in-arduino-ide-windows-instructions/

## Bereitstellung der verwendeten Bibliotheken

Um die hier verwendeten Bibliotheken mit den zugehörigen weiteren Abhängigkeiten
nicht mühsam einzeln suchen zu müssen werden diese hier bereit gestellt.

Wenn man die Arduino IDE startet kann man unter dem Menüpunkt "File/ Preferences" 
das Fenster für die Einstellungen öffnen. Hinter dem Eintrag unter "Sketchbook location"
gibt es einen Ordner libraries, in diesen sind die hier abgelegten Bibliotheken zu kopieren

## Einstellungen

Unter dem Menüeintrag "Tools/Board" öffnet sich ein Menü, hier ist der Eintrag "ESP32C3 Dev Module" auszuwählen.
Für das Flashen und die Einstellungen der seriellen Schnittstelle ist der Menüeintrag "Port" zu wählen.

## Datum
Diese Datei mit den genannten Links wurde am 09.05.2025 erstellt.

## Sonstiges
Die jeweiligen Nutzungsbdingungen sind unter den originalen Ablageorten zu finden.
