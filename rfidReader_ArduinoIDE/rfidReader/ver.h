// version history
const char* sw_version = "0.3.2";
/* 
 * Reihenfolge major.minor.bugfix
 *
 * neuste Änderungen sollen an den Beginn der Liste geschrieben werden
 * 0.3.2
 * -) UseCase AddTag im State Idle den Wechsel zwischen den rfid und dem UseCase-Bild berichtigt
 *
 * 0.3.1
 * -) In getUseCase die Abkürzung "A" ersetzt jetzt "AddTag"
 *
 * 0.3.0
 * -) Implementierung des UseCase "AddTag" (A), damit die rfidTags in die Card-Tabelle eingelesen 
 *    werden können und ein geschlossener Pool an IDs zur Verfügung steht
 *
 * 0.2.1
 * -) erneuter Aufbau des Breadboards - Anpassung der vorhergehenden Pin-Defintiotionen
 * -) kleine Änderungen bzgl der Tonausgabe, wenn die Antwort vom Server kommt 
 *
 * 0.2.0
 * -) UseCase SwitchBox implementiert, 
 *     bei positiver Rückmeldung vom Server wird über das Funkrelais die Stromversorgung für das Werkzeug eingeschaltet 
 *     und bei der Abmeldung auch wieder ausgeschaltet. *
 * 0.1.1
 * -) in der Datei support.ino in der Funktion void evalTouchAction() = ersetzt durch == 
 *    und ebenfalls den UseCase Counter hinzugefügt
 *
 * 0.1.0
 * -) Darstellung der SW Version beim Start des rfidReaders
 * -) Für den UseCase "GateKeeper", ist es möglich mit "Weiter mit Touch" die Darstellung abzubrechen, damit ein Stau von Kunden vor dem Gerät vermieden wird 
 *
 * 0.0.0 allgemeiner Ansatz
 * Beinhaltet folgende UseCases 
 * -) Counter     Übertragung der RFID in eine Tabelle der Datenbank für die Registrierung am Tresen
 * -) Gatekeeper  Zeiterfassung für den Kunden wie lange er sich im Werkstattbereich aufhält 
 * 
 *
 */