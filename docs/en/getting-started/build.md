# Build and Integration Tests

After configuring your Maven project, verify that it builds successfully:

```bash
mvn clean package
```

## JIRA-dependent Integration Tests

A small subset of ATAF core tests exercises the HTTP client against a JIRA-like endpoint (e.g. `jira.example.org`). To keep the default build self-contained and CI-friendly, these tests are **disabled by default** and gated behind a system property:

- Property: `ataf.it.jira.enabled`
- Default: `false` (property unset).
- Effect:
  - When `ataf.it.jira.enabled` is not set or is `false`, the JIRA integration tests are **skipped**.
  - When `ataf.it.jira.enabled=true`, the tests run and attempt to reach the configured JIRA URL.

### Examples

```bash
# Default build (JIRA tests skipped)
mvn clean install
```

```bash
# Run including JIRA-dependent tests (requires a suitable JIRA test environment)
mvn clean install -Dataf.it.jira.enabled=true
```

## Android Device Tests

Tests of the `mobile` module that need a real Android device are **disabled by default** as well, so regular builds and pull-request checks run without hardware:

- `-Dataf.it.mobile.enabled=true` enables the device smoke test of the `mobile` module.
- The Maven profile `android-device` runs the Cucumber scenarios of `examples/mobile-android-demo`.

```bash
# Device smoke test (requires a running Appium server and a connected device)
mvn -pl mobile -am test -Dataf.it.mobile.enabled=true -Dtestautomation.mobile.udid=<adb serial>
```

Host setup and the other commands are described in [Mobile App Testing (Android)](../usage/mobile-android.md).

For full Jira configuration including the runtime credentials passed to ATAF at execution time, see [Property Files](../configuration/properties.md) and [Runtime Credentials](../configuration/credentials.md).
