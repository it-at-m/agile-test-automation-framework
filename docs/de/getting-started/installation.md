# Installation

Füge die folgenden Maven-Abhängigkeiten nach Bedarf zur `pom.xml` deines Projekts hinzu.

Pinne eine explizite Version wie `0.3.4`. Wird das Version-Tag weggelassen, verwendet Maven die jeweils neueste verfügbare Version – das ist **nicht empfohlen** (siehe [Releases](../overview/releases.md)).

## Core-Paket

Für die Nutzung von ATAF erforderlich. Enthält die wesentliche Funktionalität für Cucumber- und Jira-Anbindung sowie hilfreiche Klassen für Testdaten und Properties.

```xml
<dependency>
    <groupId>de.muenchen.ataf</groupId>
    <artifactId>core</artifactId>
    <version>0.3.4</version>
</dependency>
```

## REST-Paket (optional)

Enthält Klassen für API-Tests.

```xml
<dependency>
    <groupId>de.muenchen.ataf</groupId>
    <artifactId>rest</artifactId>
    <version>0.3.4</version>
</dependency>
```

## Web-Paket (optional)

Enthält Klassen für Browser-basierte Tests.

```xml
<dependency>
    <groupId>de.muenchen.ataf</groupId>
    <artifactId>web</artifactId>
    <version>0.3.4</version>
</dependency>
```

## Mobile-Paket (optional)

Enthält Klassen für Tests nativer Android-Apps mit Appium und UiAutomator2. Das Modul ist neu und wird mit dem nächsten Release veröffentlicht; bis dahin baust du es lokal mit `mvn install` und verwendest `0.3.5-SNAPSHOT`. Siehe [Mobile-App-Tests (Android)](../usage/mobile-android.md).

```xml
<dependency>
    <groupId>de.muenchen.ataf</groupId>
    <artifactId>mobile</artifactId>
    <version>0.3.5-SNAPSHOT</version>
</dependency>
```

Nach der Konfiguration der Abhängigkeiten geht es weiter mit [Build und Integrationstests](./build.md).
