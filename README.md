<div id="top"></div>

<!-- PROJECT SHIELDS -->

[![Made with love by it@M][made-with-love-shield]][itm-opensource]
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![Java 21][java-shield]][java-url]
[![Cucumber][cucumber-shield]][cucumber-url]
[![TestNG][testng-shield]][testng-url]
[![JUnit 5][junit-shield]][junit-url]

---

# Agile Test Automation Framework (ATAF)

## Documentation

- Published developer handbook (GitHub Pages root): [https://it-at-m.github.io/agile-test-automation-framework/](https://it-at-m.github.io/agile-test-automation-framework/)
- Releases (used in place of an in-repo changelog): [github.com/it-at-m/agile-test-automation-framework/releases](https://github.com/it-at-m/agile-test-automation-framework/releases)
- Maven coordinates: `de.muenchen.ataf:{core,rest,web}:0.3.4` on Maven Central (after the next release)

### Creating a release

1. Open the **Actions** tab and select **Release Maven**.
2. Use `0.3.4` as the `releaseVersion`.
3. Use `0.3.5-SNAPSHOT` as the `developmentVersion`.
4. Run the workflow (signs and deploys to Maven Central; may open a follow-up PR for the next snapshot).

Details: [Releases handbook](https://it-at-m.github.io/agile-test-automation-framework/overview/releases.html).

## About ATAF

<img width="200" align="right" alt="ATAF logo" src="docs/img/ataf_logo.png" />

The **Agile Test Automation Framework (ATAF)** is a Java 21 framework for Cucumber-based BDD tests and traditional TestNG/JUnit test suites, with optional Jira/Xray integration. It is designed for agile projects: fast setup, maintainable test automation, and integration into modern development workflows. In addition to browser-based and API testing, it provides hooks for managing test executions in Jira and Xray.

The framework was developed in 2023–2024 to support the first iteration of the **ZMS / eAppointment** project of the Landeshauptstadt München and is today used in the `zmsautomation` test suite as well as other Munich projects. It was built primarily by colleagues from **digital@M** for use at **it@M**.

It provides:

- Support for both BDD testing with Cucumber and traditional test cases with TestNG and JUnit
- Seamless integration with popular testing libraries
- Easy-to-configure runners for TestNG and JUnit
- Integration with Jira and Xray using their existing REST APIs for test management

For prerequisites, installation, writing and running tests, property files, supported webdrivers, reporting, and the full project history see the [handbook](https://it-at-m.github.io/agile-test-automation-framework/).

## Contact

[Overview](https://opensource.muenchen.de/)

Munich contact: it@M – opensource@muenchen.de

ATAF was built primarily by colleagues from **digital@M** for use at **it@M**, the IT service provider of the Landeshauptstadt München.

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td style="padding-right: 30px;"><img src="https://raw.githubusercontent.com/it-at-m/agile-test-automation-framework/main/docs/img/digital-at-m-logo.png" height="30" align="center" alt="digital@M"></td>
    <td style="padding-right: 30px;"><img src="https://assets.muenchen.de/logos/itm/itM_Basislogo_gelb_schwarz-500.png" height="30" align="center" alt="it@M"></td>
    <td><img src="https://assets.muenchen.de/logos/lhm/logo-lhm-muenchen.svg" height="30" align="center" alt="Landeshauptstadt München"></td>
  </tr>
</table>

---

## Über ATAF

<img width="200" align="right" alt="ATAF-Logo" src="docs/img/ataf_logo.png" />

Das **Agile Test Automation Framework (ATAF)** ist ein Java-21-Framework für Cucumber-basierte BDD-Tests und klassische TestNG/JUnit-Testsuites mit optionaler Jira-/Xray-Anbindung. Es wurde für agile Projekte entworfen: schnelle Einrichtung, wartbare Testautomatisierung und Integration in moderne Entwicklungs-Workflows. Neben Browser- und API-Tests bietet es Hooks für die Verwaltung von Testausführungen in Jira und Xray.

Das Framework wurde 2023–2024 entwickelt, um die erste Iteration des **ZMS-/eAppointment**-Projekts der Landeshauptstadt München zu unterstützen, und wird heute in der `zmsautomation`-Testsuite sowie weiteren Münchner Projekten eingesetzt. Es wurde überwiegend von Kolleg:innen aus **digital@M** für den Einsatz bei **it@M** entwickelt.

Es bietet:

- Unterstützung sowohl für BDD-Tests mit Cucumber als auch für klassische Testfälle mit TestNG und JUnit
- Nahtlose Integration in verbreitete Test-Bibliotheken
- Einfach konfigurierbare Runner für TestNG und JUnit
- Anbindung an Jira und Xray über deren REST-APIs für das Testmanagement

Voraussetzungen, Installation, Tests schreiben und ausführen, Property-Dateien, unterstützte Webdriver, Reporting und die vollständige Projektgeschichte stehen im [Handbuch](https://it-at-m.github.io/agile-test-automation-framework/de/).

## Kontakt

[Übersicht](https://opensource.muenchen.de/)

Münchner Kontakt: it@M – opensource@muenchen.de

ATAF wurde überwiegend von Kolleg:innen aus **digital@M** für den Einsatz bei **it@M**, dem IT-Dienstleister der Landeshauptstadt München, entwickelt.

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td style="padding-right: 30px;"><img src="https://raw.githubusercontent.com/it-at-m/agile-test-automation-framework/main/docs/img/digital-at-m-logo.png" height="30" align="center" alt="digital@M"></td>
    <td style="padding-right: 30px;"><img src="https://assets.muenchen.de/logos/itm/itM_Basislogo_gelb_schwarz-500.png" height="30" align="center" alt="it@M"></td>
    <td><img src="https://assets.muenchen.de/logos/lhm/logo-lhm-muenchen.svg" height="30" align="center" alt="Landeshauptstadt München"></td>
  </tr>
</table>

## Dokumentation (Deutsch)

- Veröffentlichtes Entwicklerhandbuch (GitHub Pages, deutsche Version): [https://it-at-m.github.io/agile-test-automation-framework/de/](https://it-at-m.github.io/agile-test-automation-framework/de/)
- Releases (ersetzt einen Changelog im Repository): [github.com/it-at-m/agile-test-automation-framework/releases](https://github.com/it-at-m/agile-test-automation-framework/releases)
- Maven-Koordinaten: `de.muenchen.ataf:{core,rest,web}:0.3.4` auf Maven Central (nach dem nächsten Release)

### Release erstellen

1. In GitHub den Reiter **Actions** öffnen und **Release Maven** wählen.
2. `0.3.4` als `releaseVersion` verwenden.
3. `0.3.5-SNAPSHOT` als `developmentVersion` verwenden.
4. Workflow starten (signiert und veröffentlicht auf Maven Central; ggf. Follow-up-PR für den nächsten Snapshot).

Details: [Releases-Handbuch](https://it-at-m.github.io/agile-test-automation-framework/de/overview/releases.html).

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->

[contributors-shield]: https://img.shields.io/github/contributors/it-at-m/agile-test-automation-framework.svg?style=for-the-badge
[contributors-url]: https://github.com/it-at-m/agile-test-automation-framework/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/it-at-m/agile-test-automation-framework.svg?style=for-the-badge
[forks-url]: https://github.com/it-at-m/agile-test-automation-framework/network/members
[stars-shield]: https://img.shields.io/github/stars/it-at-m/agile-test-automation-framework.svg?style=for-the-badge
[stars-url]: https://github.com/it-at-m/agile-test-automation-framework/stargazers
[issues-shield]: https://img.shields.io/github/issues/it-at-m/agile-test-automation-framework.svg?style=for-the-badge
[issues-url]: https://github.com/it-at-m/agile-test-automation-framework/issues
[license-shield]: https://img.shields.io/github/license/it-at-m/agile-test-automation-framework.svg?style=for-the-badge
[license-url]: https://github.com/it-at-m/agile-test-automation-framework/blob/main/LICENSE
[made-with-love-shield]: https://img.shields.io/badge/made%20with%20%E2%9D%A4%20by-it%40M-yellow?style=for-the-badge
[itm-opensource]: https://opensource.muenchen.de/
[java-shield]: https://img.shields.io/badge/Java-21-orange?style=for-the-badge
[java-url]: https://www.oracle.com/java/
[cucumber-shield]: https://img.shields.io/badge/Cucumber-BDD-brightgreen?style=for-the-badge
[cucumber-url]: https://cucumber.io/
[testng-shield]: https://img.shields.io/badge/TestNG-supported-red?style=for-the-badge
[testng-url]: https://testng.org/
[junit-shield]: https://img.shields.io/badge/JUnit-5-blue?style=for-the-badge
[junit-url]: https://junit.org/junit5/
