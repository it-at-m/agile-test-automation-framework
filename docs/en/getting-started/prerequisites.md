# Prerequisites

Make sure the following software is installed before working with ATAF:

- **Java SDK 21** or newer.
- **Maven** (any reasonably recent 3.x).
- An IDE such as **IntelliJ IDEA** or **Eclipse**.

## Verifying Your Setup

```bash
java -version
mvn -version
```

Both commands should report a version. Java should be 21 or newer.

## Optional Tooling

- A **Selenium Grid** (or BrowserStack-like remote endpoint) if you plan to run browser tests against a shared infrastructure. The endpoint URL is configured through `testautomation.seleniumGridUrl` in [`testautomation.properties`](../configuration/properties.md#testautomation-properties).
- A **Jira / Xray** instance if you plan to use ATAF's Jira integration. The framework only requires Jira/Xray REST endpoints, an auth token, and a small number of issue type and custom field IDs. See [`jira.properties`](../configuration/properties.md#jira-properties) and [Runtime Credentials](../configuration/credentials.md).
