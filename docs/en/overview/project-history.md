# Project History

## Context

The **Agile Test Automation Framework (ATAF)** was developed in **2023–2024** to support the first iteration of the **ZMS / eAppointment** project — the appointment-scheduling system operated by the City of Munich (Landeshauptstadt München).

After that initial use, ATAF was extracted into its own repository so that it could be reused across teams. It is today used in the [`zmsautomation`](https://github.com/it-at-m/eappointment/tree/main/zmsautomation) test suite of the eAppointment project and is reused internally by other Munich projects.

## Origin

ATAF was built primarily by colleagues from **digital@M** — the in-house design and engineering unit for digital products at the City of Munich — for use at **it@M**, the IT service provider of the Landeshauptstadt München.

The framework consolidates patterns that had emerged across multiple Munich projects:

- Cucumber-based BDD with TestNG/JUnit underneath
- Selenium- and REST-based test layers
- Optional integration with Jira / Xray for test management
- A property-driven configuration model that works across local, CI, and grid setups

## Today

- **In production use**: `zmsautomation` (ZMS / eAppointment end-to-end and REST tests)
- **Internal reuse**: additional Munich projects consume `de.muenchen.ataf:{core,rest,web}` from Maven Central
- **Open source**: the framework is published under MIT on [GitHub](https://github.com/it-at-m/agile-test-automation-framework) and on [opensource.muenchen.de](https://opensource.muenchen.de/)
