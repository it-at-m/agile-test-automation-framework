# Projektgeschichte

## Kontext

Das **Agile Test Automation Framework (ATAF)** wurde **2023–2024** entwickelt, um die erste Iteration des **ZMS-/eAppointment**-Projekts zu unterstützen – das Terminvergabesystem der Landeshauptstadt München.

Nach dem ersten Einsatz wurde ATAF in ein eigenes Repository ausgelagert, damit es teamübergreifend wiederverwendet werden kann. Heute kommt es in der [`zmsautomation`](https://github.com/it-at-m/eappointment/tree/main/zmsautomation)-Testsuite des eAppointment-Projekts zum Einsatz und wird intern in weiteren Münchner Projekten genutzt.

## Entstehung

ATAF wurde überwiegend von Kolleg:innen aus **digital@M** – der hauseigenen Design- und Engineering-Einheit der Landeshauptstadt München – für den Einsatz bei **it@M**, dem IT-Dienstleister der Landeshauptstadt München, entwickelt.

Das Framework fasst Muster zusammen, die sich in mehreren Münchner Projekten herausgebildet hatten:

- Cucumber-basierte BDD-Tests mit TestNG/JUnit darunter
- Selenium- und REST-basierte Testebenen
- Optionale Anbindung an Jira / Xray für das Testmanagement
- Ein property-gesteuertes Konfigurationsmodell für lokale, CI- und Grid-Setups

## Heute

- **Produktiver Einsatz**: `zmsautomation` (End-to-End- und REST-Tests von ZMS / eAppointment)
- **Interne Nutzung**: weitere Münchner Projekte verwenden `de.muenchen.ataf:{core,rest,web}` von Maven Central
- **Open Source**: das Framework ist unter MIT-Lizenz auf [GitHub](https://github.com/it-at-m/agile-test-automation-framework) und auf [opensource.muenchen.de](https://opensource.muenchen.de/) veröffentlicht
