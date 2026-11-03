# Voraussetzungen

Stelle sicher, dass die folgende Software installiert ist, bevor du mit ATAF arbeitest:

- **Java SDK 21** oder neuer.
- **Maven** (ein hinreichend aktuelles 3.x).
- Eine IDE wie **IntelliJ IDEA** oder **Eclipse**.

## Einrichtung prüfen

```bash
java -version
mvn -version
```

Beide Befehle sollten eine Version melden. Java sollte mindestens 21 sein.

## Optionale Werkzeuge

- Ein **Selenium Grid** (oder ein BrowserStack-ähnlicher Remote-Endpunkt), falls du Browser-Tests gegen eine gemeinsame Infrastruktur ausführen willst. Die Endpunkt-URL wird über `testautomation.seleniumGridUrl` in den [`testautomation.properties`](../configuration/properties.md#testautomation-properties) konfiguriert.
- Ein **Android-Gerät** (Android 8.0 oder neuer), angeschlossen an einen Linux-Host mit den **Android-Platform-Tools** (`adb`), **Node.js** und einem **Appium-3**-Server mit UiAutomator2-Treiber, falls du native Android-Apps mit dem Modul `mobile` testen willst. Siehe [Mobile-App-Tests (Android)](../usage/mobile-android.md).
- Eine **Jira-/Xray**-Instanz, falls du ATAFs Jira-Integration nutzen willst. Das Framework benötigt lediglich Jira-/Xray-REST-Endpunkte, einen Auth-Token und einige Issue-Typ- und Custom-Field-IDs. Siehe [`jira.properties`](../configuration/properties.md#jira-properties) und [Laufzeit-Zugangsdaten](../configuration/credentials.md).
