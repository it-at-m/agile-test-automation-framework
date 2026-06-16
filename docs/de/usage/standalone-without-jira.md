# Standalone-Nutzung (ohne Jira, lokales Keycloak)

ATAF wurde für agile Teams entwickelt, die Tests in Jira und Xray pflegen — **das ist aber nicht zwingend**. Du kannst das Framework als klassischen Cucumber- + Selenium- + REST-Assured-Stack nutzen: Feature-Dateien liegen im Git-Repository, Reports bleiben unter `target/`, und UI-Tests authentifizieren sich gegen ein **lokales Keycloak** statt gegen eine interne oder behördliche SSO-Umgebung.

So ist die [zmsautomation](https://it-at-m.github.io/eappointment/de/testing-and-automation/zmsautomation.html)-Testsuite im eAppointment-Projekt heute aufgebaut.

## Ohne Jira und Xray

Die Jira-Anbindung ist **optional**. ATAF spricht nur dann mit Jira, wenn du Zugangsdaten (`auth_token` oder `username`/`password`) bereitstellst **und** einen Jira-Workflow konfigurierst (z. B. über `jira.properties` und Surefire-Properties wie `issueKeys` oder `filterId`). Fehlen diese, bleiben lokale Feature-Dateien aktiv und Xray-Testausführungen werden nicht aktualisiert.

### Feature-Dateien im Repository ablegen

Lege `.feature`-Dateien unter `src/test/resources/features/` ab (oder unter einem in `cucumber.properties` gesetzten Pfad):

```properties
cucumber.features=src/test/resources/features
cucumber.glue=your.project.steps,ataf.web.steps
cucumber.plugin=json:target/cucumber.json,html:target/site/cucumber-pretty
```

Führe Tests wie gewohnt mit Maven aus — kein Jira-Export-Schritt läuft vorher.

### Was du weglassen kannst

| Jira-basierter Aufbau                                  | Standalone-Aufbau                                                                                    |
| ------------------------------------------------------ | ---------------------------------------------------------------------------------------------------- |
| `jira.properties`                                      | nicht erforderlich                                                                                   |
| Laufzeit-`auth_token`, `username`, `password` für Jira | nicht erforderlich ([Laufzeit-Zugangsdaten](../configuration/credentials.md) betrifft nur Jira/Xray) |
| Feature-Dateien in Jira/Xray                           | Feature-Dateien in Git                                                                               |
| Xray-Testausführung nach jedem Lauf                    | nur Cucumber-HTML/JSON + Surefire-Reports ([Reporting](../reporting.md))                             |

Das ATAF-`core`-Modul bringt weiterhin Jira-/Xray-Helfer mit, aber Hooks wie `RunnerUtils.isJiraBasedTestExecution()` bleiben inaktiv, solange Features von der Festplatte geladen werden.

Siehe [Tests schreiben](./writing-tests.md) für Cucumber- und TestNG-/JUnit-Beispiele.

## Ohne Corporate- oder Behörden-SSO

UI-Tests mit OpenID-Connect-Login brauchen **keinen** Zugang zu einem internen `ssodev` oder Produktions-IdP. Dasselbe Muster wie im [RefArch-Stack](https://github.com/it-at-m/refarch-templates/tree/main/stack/keycloak/migration) funktioniert für ATAF:

1. **Keycloak** lokal starten (Docker Compose).
2. Realm-Konfiguration mit **[keycloakmigration](https://github.com/klg71/keycloakmigration)** anwenden (Image `klg71/keycloakmigration`).
3. Anwendung und ATAF-Test-Properties auf lokalen Realm, Clients und Testbenutzer ausrichten.

Die RefArch-Templates definieren einen minimalen lokalen Stack in [`stack/docker-compose.yml`](https://github.com/it-at-m/refarch-templates/blob/4735e9f425a29e9cd38eafc6cd34b5da705f0574/stack/docker-compose.yml) — Keycloak auf Port `8100`, ein `init-keycloak`-Sidecar wartet auf Keycloak und wendet YAML-Migrationen aus `stack/keycloak/migration/` an.

### Typischer lokaler Keycloak-Aufbau

```text
keycloak          # quay.io/keycloak/keycloak — start-dev, KC_HTTP_RELATIVE_PATH=/auth
init-keycloak     # klg71/keycloakmigration — wendet KEYCLOAK_CHANGELOG beim ersten Start an
migration/        # YAML-Changelog (Realm, Clients, Rollen, Benutzer)
```

Umgebungsvariablen für `init-keycloak` entsprechen dem RefArch-Beispiel:

- `ADMIN_USER` / `ADMIN_PASSWORD` — Keycloak-Bootstrap-Admin
- `BASEURL` — z. B. `http://keycloak:8080/auth` (im Container) oder `http://keycloak:8100/auth` (RefArch-Port)
- `WAIT_FOR_KEYCLOAK=true`
- `KEYCLOAK_CHANGELOG=/migration/keycloak-changelog.yml`

Trage ATAF-UI-Zugangsdaten in `testautomation.properties` (oder per Umgebung) ein, passend zu einem Benutzer aus der Migration, z. B.:

```properties
testautomation.userName=ataf
testautomation.userPassword=vorschau
```

Ergänze interne Hostnamen in `testautomation.noProxy`, wenn der Browser hinter einem Corporate-HTTP-Proxy läuft, Docker-Dienste aber direkt erreichen muss (siehe [zmsautomation](https://it-at-m.github.io/eappointment/de/testing-and-automation/zmsautomation.html#lokales-keycloak-setup) für ein konkretes Beispiel).

## Referenz: zmsautomation in eAppointment

Die [zmsautomation-Dokumentation](https://it-at-m.github.io/eappointment/de/testing-and-automation/zmsautomation.html) beschreibt das vollständige Setup: DDEV/devcontainer, lokale Keycloak-Migrationen, Flyway-Testdaten und ATAF-Profile (`ataf-api`, `ataf-ui`).

Kernpunkte:

- **Keine `jira.properties`** — alle Szenarien liegen unter `zmsautomation/src/test/resources/features/`.
- **Lokales Keycloak** — `.resources/keycloak/migration/` (changelog-gesteuerter Realm `zms`, Clients, Rollen, Benutzer `ataf` / `vorschau` für UI-Tests).
- **Docker Compose** — Keycloak + `init-keycloak` in [`.ddev/docker-compose.keycloak.yaml`](https://github.com/it-at-m/eappointment/blob/main/.ddev/docker-compose.keycloak.yaml) und [`.devcontainer/docker-compose.yaml`](https://github.com/it-at-m/eappointment/blob/main/.devcontainer/docker-compose.yaml).
- **Host-Mapping** — `127.0.0.1 keycloak` in `/etc/hosts`, damit Browser-Redirects funktionieren ([Lokale Keycloak-Einrichtung](https://it-at-m.github.io/eappointment/de/setup-and-development/local-keycloak-setup.html) in der eAppointment-Doku).

Für ein neues Projekt: zuerst das RefArch-Migrationsmuster übernehmen, dann ATAF-Properties auf die von deiner Anwendung erwarteten Benutzer und Redirect-URIs ausrichten.
