# Reporting

Nach der Testausführung werden Reports im Verzeichnis `target/surefire-reports` erzeugt. Typischerweise findest du:

- **HTML-Report** – detaillierte Übersicht der Testausführung.
- **JUnit-XML-Report** – XML-Format für die Integration in CI-Tools.

Ist die Jira-Integration aktiv, wird zusätzlich die zugehörige Jira-Xray-Test-Execution direkt mit den Testergebnissen aktualisiert.

## Cucumber-Reports

Mit der Default-Plugin-Konfiguration aus [Property-Dateien → `cucumber.properties`](./configuration/properties.md#cucumber-properties) werden folgende Reports erzeugt:

- `target/cucumber.json` – JSON-Report (maschinenlesbar; lässt sich an weitere Tools weiterreichen).
- `target/site/cucumber-pretty` – HTML-Report.

## CI-Integration

Die JUnit-XML-Ausgabe macht die Reports out of the box mit den meisten CI-Dashboards kompatibel:

- GitHub Actions: `target/surefire-reports/**/*.xml` mit [test-reporter](https://github.com/dorny/test-reporter) oder einer äquivalenten Action hochladen.
- GitLab CI: das Feld [`junit:` artifact reports](https://docs.gitlab.com/ci/yaml/artifacts_reports/#artifactsreportsjunit) nutzen.
- Jenkins: das JUnit-Plugin verwenden.
