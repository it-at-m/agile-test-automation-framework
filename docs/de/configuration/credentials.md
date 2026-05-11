# Laufzeit-Zugangsdaten

Die folgenden Properties sollten ausschließlich zur Test-Laufzeit übergeben werden und **niemals ins Repository eingecheckt** werden. Sie enthalten Zugangsdaten.

```properties
# Passwort zum Ver-/Entschlüsseln von Testdaten zur Laufzeit
taf.testDataEncryptionPassword=<dein Testdaten-Passwort>

# Auth-Token des technischen Jira-Users, für die Kommunikation mit Jira/Xray
auth_token=<dein Jira-Auth-Token>

# LDAP- oder technischer Benutzername als Fallback für die Jira-Kommunikation
# und zur Bestimmung des Assignees
username=<technischer Benutzername>

# Passwort des technischen Users als Fallback für die Jira-Kommunikation
password=<Passwort des technischen Users>
```

## Beispiel-CLI-Aufruf

```bash
mvn clean test \
  -Dtaf.testDataEncryptionPassword=<dein Testdaten-Passwort> \
  -Dauth_token=<dein Jira-Auth-Token> \
  -Dusername=<technischer Benutzername> \
  -Dpassword=<Passwort des technischen Users>
```

## CI-Empfehlungen

- Übergib jede Property über ein CI-Secret (GitHub Actions, GitLab-CI-Variablen etc.) – nicht inline im Workflow-File.
- Maskiere die Werte in CI-Logs.
- Rotiere den Jira-Auth-Token des technischen Users regelmäßig; ATAF liest den Wert bei jedem Testlauf neu ein.
- Halte `taf.testDataEncryptionPassword` über Umgebungen hinweg konsistent, die denselben verschlüsselten Testdatensatz teilen.
