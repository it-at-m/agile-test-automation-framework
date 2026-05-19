# Roadmap

## Ambition

The long-term goal for ATAF is to become a **city-wide test framework for both UI and REST applications** at the Landeshauptstadt München — a shared foundation that any team at it@M can pick up to automate end-to-end, browser, and API tests without rebuilding the same boilerplate in every project.

A growing pool of reusable building blocks already ships with the `web` and `rest` modules and can be dropped into most applications as-is. Two examples from the `web` module:

- [`SingleSignOnPage`](https://github.com/it-at-m/agile-test-automation-framework/blob/main/web/src/main/java/ataf/web/pages/SingleSignOnPage.java) — generic Keycloak/SSO login flow that works against any Keycloak realm by parameterising the username field, password field, and login-button locators.
- [`RandomNameGenerator`](https://github.com/it-at-m/agile-test-automation-framework/blob/main/web/src/main/java/ataf/web/pages/RandomNameGenerator.java) — fetches a realistic random first name and surname from an external generator and exposes them (including an email-conform form) for test-data setup.

The roadmap is about growing that pool — and the framework itself — until any LHM team can wire ATAF into a new project and immediately have working SSO, test data, reporting, and Jira/Xray integration.

See the [open issues](https://github.com/it-at-m/agile-test-automation-framework/issues) for the current list of proposed features and known issues.
