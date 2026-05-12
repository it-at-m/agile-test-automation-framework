# Roadmap

## Ambition

Das langfristige Ziel von ATAF ist es, ein **stadtweites Test-Framework für UI- und REST-Anwendungen** der Landeshauptstadt München zu werden — eine gemeinsame Grundlage, die jedes Team bei it@M aufgreifen kann, um End-to-End-, Browser- und API-Tests zu automatisieren, ohne dieselben Standardbausteine in jedem Projekt neu schreiben zu müssen.

Eine wachsende Sammlung wiederverwendbarer Bausteine ist bereits Teil der Module `web` und `rest` und lässt sich in den meisten Anwendungen direkt einsetzen. Zwei Beispiele aus dem `web`-Modul:

- [`SingleSignOnPage`](https://github.com/it-at-m/agile-test-automation-framework/blob/main/web/src/main/java/ataf/web/pages/SingleSignOnPage.java) — generischer Keycloak-/SSO-Loginflow, der gegen beliebige Keycloak-Realms funktioniert, indem Locator für Benutzername-Feld, Passwort-Feld und Login-Button parametrisiert werden.
- [`RandomNameGenerator`](https://github.com/it-at-m/agile-test-automation-framework/blob/main/web/src/main/java/ataf/web/pages/RandomNameGenerator.java) — holt einen realistischen Vor- und Nachnamen von einem externen Generator und stellt sie (inklusive einer E-Mail-konformen Variante) für den Testdaten-Aufbau bereit.

Die unten stehende Roadmap zielt darauf ab, diese Sammlung — und das Framework selbst — so weit auszubauen, dass jedes LHM-Team ATAF in ein neues Projekt einbinden und sofort über funktionierende SSO-, Testdaten-, Reporting- und Jira/Xray-Integration verfügen kann.

## Mögliche zukünftige Erweiterungen

- Erweiterte Unterstützung weiterer Programmiersprachen wie Python, C#, Ruby und JavaScript.
- Zusätzliche wiederverwendbare Step-Bibliotheken.
- Verbesserte Reporting-Integrationen.
- Modularere Plugins für projektspezifische Erweiterungen.

Eine vollständige Liste vorgeschlagener Features und bekannter Probleme findest du in den [offenen Issues](https://github.com/it-at-m/agile-test-automation-framework/issues).
