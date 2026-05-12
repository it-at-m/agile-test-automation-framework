# Property-Dateien und unterstützte Webdriver

Die meisten Testeinstellungen werden über Java-Property-Dateien konfiguriert. Lege sie in deinem Test-Ressourcenverzeichnis ab, z. B. `src/test/resources`.

Diese Seite listet die vier Dateien auf, die ATAF erwartet, und gibt für jede ein kommentiertes Beispiel. Die folgende Tabelle fasst zusammen, welche Browser und Architekturen das `web`-Modul ansteuern kann – maßgebliche Quelle ist [`DriverUtil`](https://github.com/it-at-m/agile-test-automation-framework/blob/main/web/src/main/java/ataf/web/utils/DriverUtil.java).

## Unterstützte Webdriver

| Browser-Schlüssel (`testautomation.browser`) | Selenium-Driver                     | Lokale Ausführung       | Selenium-Grid-`platformName`                                           | Architekturen                                                | Headless                                   |
| -------------------------------------------- | ----------------------------------- | ----------------------- | ---------------------------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------ |
| `chrome`                                     | `ChromeDriver`                      | Windows · macOS · Linux | `LINUX`                                                                | `amd64` (Intel/x86_64), `arm64`                              | ja – `testautomation.browserHeadless=true` |
| `edge`                                       | `EdgeDriver`                        | Windows · macOS · Linux | konfigurierbar über `testautomation.platformName` (Standard `WINDOWS`) | `amd64`, `arm64`                                             | ja – `testautomation.browserHeadless=true` |
| `firefox`                                    | `GeckoDriver` (via `FirefoxDriver`) | Windows · macOS · Linux | `LINUX`                                                                | `amd64`, `arm64`                                             | immer aktiv im Grid-Betrieb                |
| `safari`                                     | `SafariDriver`                      | nur macOS (10.15+)      | `MAC`                                                                  | `amd64` (Intel macOS/iOS), `arm64` (Apple Silicon macOS/iOS) | von Safari nicht unterstützt               |

Hinweise:

- Der Browser wird über `testautomation.browser` ausgewählt (siehe [`testautomation.properties`](#testautomation-properties) weiter unten).
- Für Grid-Läufe vergibt ATAF die `platformName`-Capability automatisch; ausschließlich bei `edge` lässt sie sich über `testautomation.platformName` überschreiben.
- Safari erfordert `testautomation.platformName=mac` und ist nur auf macOS-/iOS-Hosts verfügbar. Details siehe Abschnitt _Safari-Unterstützung_ weiter unten.
- Proxy, Inkognito-/Privatmodus, das Akzeptieren unsicherer Zertifikate und die eager Page-Load-Strategie werden pro Browser innerhalb von `DriverUtil` gesetzt.

[[toc]]

## `cucumber.properties`

Steuert das Verhalten von Cucumber.

```properties
# Deaktiviert das Veröffentlichen von Test-Reports
cucumber.publish.enabled=false

# Unterdrückt zusätzliche Publishing-Ausgaben
cucumber.publish.quiet=true

# Pfad zu den Cucumber-Feature-Dateien
cucumber.features=src/test/resources/features

# Erzeugt sowohl JSON- als auch HTML-Reports
cucumber.plugin=json:target/cucumber.json,html:target/site/cucumber-pretty

# Kommagetrennte Pakete mit Cucumber-Step-Klassen
cucumber.glue=test.automation.framework.steps,ataf.web.steps
```

## `jira.properties`

Diese Properties werden für die Jira-Integration verwendet. Manche Werte müssen je nach Jira-Setup über die Jira-REST-API ermittelt werden.

```properties
# Projekt-ID als numerischer Wert
jira.test.execution.project.id=12345678

# Summary-Text für generierte Test Executions
jira.test.execution.summary=Automatically generated test execution

# Issue-Typ-ID für Test Executions
jira.test.execution.issuetype.id=-1

# Label, mit dem das Framework Test Executions identifiziert
jira.test.execution.labels.automation.label=automated

# Umgebung des Tests; gilt nur für generierte Test Executions
jira.test.execution.test.environment=test

# Custom-Field-ID des Test-Environment-Felds in einem Test-Execution-Issue
jira.test.execution.test.environment.customfield.id=customfield_-1

# Custom-Field-ID des Test-Plan-Felds in einem Test-Execution-Issue
jira.test.execution.test.plan.customfield.id=customfield_-1

# Transition-ID, um ein Issue auf "In Progress" zu setzen
jira.test.execution.transition.id.in.progress=1

# Label, mit dem das Framework Test Executions als "in Bearbeitung" markiert
jira.test.execution.labels.in.progress=inProgress

# Transition-ID, um ein Issue auf "Done" zu setzen
jira.test.execution.transition.id.done=3
```

## `log4j2-test.properties`

Konfiguriert das Logger-Verhalten.

```properties
appenders=console,threadFile

appender.console.type=Console
appender.console.name=STDOUT
appender.console.layout.type=PatternLayout
appender.console.layout.pattern=[thread-id %T] %d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
appender.console.layout.charset=UTF-8

appender.threadFile.type=RollingFile
appender.threadFile.name=ThreadLog
appender.threadFile.fileName=logs/${ctx:scenario:-default}.log
appender.threadFile.filePattern=logs/${ctx:scenario:-default}-%d{yyyy-MM-dd}.log.gz
appender.threadFile.layout.type=PatternLayout
appender.threadFile.layout.pattern=[thread-id %T] %d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
appender.threadFile.layout.charset=UTF-8
appender.threadFile.policies.type=Policies
appender.threadFile.policies.time.type=TimeBasedTriggeringPolicy

rootLogger.level=info
rootLogger.appenderRefs=stdout,threadLog
rootLogger.appenderRef.stdout.ref=STDOUT
rootLogger.appenderRef.threadLog.ref=ThreadLog
```

## `testautomation.properties`

```properties
# Für Tests verwendeter Browser (mögliche Werte: firefox, chrome, edge, safari)
testautomation.browser=firefox

# Zielplattform der WebDriver-Session. Wird für Safari benötigt,
# da Safari ausschließlich auf macOS/iOS verfügbar ist (z. B. mac, ios).
testautomation.platformName=mac

# Browser-Version für die Tests
testautomation.browserVersion=140.7.0

# URL des Selenium-Grid-Hubs für die Remote-Ausführung
testautomation.seleniumGridUrl=https://selenium.example.com/wd/hub

# Soll für Tests ein Proxy verwendet werden (true/false)
testautomation.boolean.useProxy=true

# Proxy-Host für Testverbindungen
testautomation.proxyAddress=192.168.100.200

# Proxy-Port für Testverbindungen
testautomation.int.proxyPort=8080

# Kommagetrennte Liste von Domains, die den Proxy umgehen
testautomation.noProxy=my-example-service.example.com

# Default-Wartezeit (ms) für Skripte und Seitenladevorgänge
testautomation.long.defaultScriptAndPageLoadTime=120000

# Default-Implicit-Wait (ms)
testautomation.long.defaultImplicitWaitTime=250

# Default-Explicit-Wait (s)
testautomation.int.defaultExplicitWaitTime=60

# Soll der Browser im Inkognito-/Privatmodus starten
testautomation.boolean.useIncognitoMode=true

# Log-Level der Testautomatisierung
testautomation.logLevel=INFO

# Browserfensterbreite in Pixeln
testautomation.int.screenWidth=1920

# Browserfensterhöhe in Pixeln
testautomation.int.screenHeight=1080

# Pfad zu Firefox-Erweiterungen für die Tests
testautomation.firefoxExtensionDirectory=./src/test/resources/extensions/firefox/

# Jira-REST-API-URLs
testautomation.jiraRestApiUrl=https://jira.example.com/rest/api/2/
testautomation.jiraXrayRestApiUrl=https://jira.example.com/rest/raven/1.0/
```

### Safari-Unterstützung

Seit [ATAF 0.3.3](https://github.com/it-at-m/agile-test-automation-framework/pull/13) kann der WebDriver in `ataf-web` Safari über `SafariDriver`/`SafariOptions` ansteuern – sowohl lokal als auch über ein Selenium Grid. Damit lassen sich Tests ausführen auf:

- `amd64`-(Intel-)macOS- und iOS-Geräten.
- `arm64`-(Apple-Silicon-)macOS- und iOS-Geräten.

Zum Testen mit Safari setzt du:

```properties
testautomation.browser=safari
testautomation.platformName=mac
```

Beispielhafte Log-Ausgabe beim Start:

```text
[1] 21:45:06 INFO  TestProperties:148 - (string) Property [platformName] with value [mac] has been successfully loaded!
[1] 21:45:06 INFO  TestProperties:148 - (string) Property [browser] with value [safari] has been successfully loaded!
```

::: warning Safari nur auf macOS/iOS
Safari steht nicht für `Windows amd64`/`Windows arm64` oder `amd64`-/`arm64`-Linux zur Verfügung. Folglich kann Safari **nicht auf Linux-GitHub-Actions-Runnern** ausgeführt werden. Für Safari-Tests in der CI bestehen derzeit zwei Optionen:

- macOS-Runner in GitHub Actions verwenden (z. B. `macos-latest` oder `macos-26-intel`; siehe den [Runner-Images-Katalog](https://github.com/actions/runner-images/blob/main/images/macos/macos-26-Readme.md#browsers)) und für Safari konfigurieren, oder
- die Safari-Läufe aus dem GitHub-Actions-Workflow heraus an einen Cloud-basierten Cross-Browser-Testing-Dienst delegieren.
  :::

Zugangsdaten, die nicht zusammen mit diesen Property-Dateien eingecheckt werden dürfen, siehe [Laufzeit-Zugangsdaten](./credentials.md).
