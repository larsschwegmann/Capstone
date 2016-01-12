# Capstone

### Vorwort

Ich habe die Netbeans Projektdateien mitgegeben. Es muss nur lanterna eingebunden werden und dann sollte es kompilieren.

Das Spiel erwartet, dass sich das zu ladene Level (bei "Neues Spiel" level_name=level.properties, bei "Spiel laden" level_name=level_saved.properties) unter user_home/.labyrinth/level_name befindet.
Unter UNIX also unter **~/.lanterna/level_name** und unter Windows z.B **C:\Users\username\.labyrinth\level_name**

Das Verzeichnis **user_home/.lanterna** wird beim ersten Start des Spiels erstellt, sofern nicht bereits vorhanden. "user_home" wird hier über `System.getProperty("user.home");` ermittelt.

Sollte "Neues Spiel"/"Spiel Laden" augewählt werden und die jeweilige Datei nicht vorhanden sein, zeigt das Programm eine Fehlermeldung. Bitte platzieren sie nach dem erstmaligem Start eine Datei namens level.properties im .labyrinth Verzeichnis.

**Es wird vom Programm erwartet, dass das Level ein gültiges (vom Generator erstelltes) Format besitzt!**

### Ausführung

Durch ausführen von "Main" wird das Spiel gestartet. Man startet im Hauptmenü.
Von hier aus kann man ein neues Spiel beginnen, ein gepeichertes Spiel laden, sich die Legende ansehen oder das Spiel beenden. Navigiert wird mit den Pfeiltasten.

Während des Spiels ist das Pause Menü jederzeit durch ESC zu erreichen.
