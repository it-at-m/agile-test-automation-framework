# Build und Integrationstests

Nach der Konfiguration deines Maven-Projekts prüfst du, ob es erfolgreich baut:

```bash
mvn clean package
```

## JIRA-abhängige Integrationstests

Ein kleiner Teil der ATAF-Core-Tests prüft den HTTP-Client gegen einen JIRA-ähnlichen Endpunkt (z. B. `jira.example.org`). Damit der Standard-Build self-contained und CI-tauglich bleibt, sind diese Tests **standardmäßig deaktiviert** und über eine System-Property gesteuert:

- Property: `ataf.it.jira.enabled`
- Standardwert: `false` (Property nicht gesetzt).
- Effekt:
  - Wenn `ataf.it.jira.enabled` nicht gesetzt oder `false` ist, werden die JIRA-Integrationstests **übersprungen**.
  - Bei `ataf.it.jira.enabled=true` werden die Tests ausgeführt und versuchen, die konfigurierte JIRA-URL zu erreichen.

### Beispiele

```bash
# Standard-Build (JIRA-Tests übersprungen)
mvn clean install
```

```bash
# Inklusive JIRA-abhängige Tests (erfordert eine geeignete JIRA-Testumgebung)
mvn clean install -Dataf.it.jira.enabled=true
```

Die vollständige Jira-Konfiguration inklusive der zur Laufzeit übergebenen Zugangsdaten findest du unter [Property-Dateien](../configuration/properties.md) und [Laufzeit-Zugangsdaten](../configuration/credentials.md).
