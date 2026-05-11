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

For full Jira configuration including the runtime credentials passed to ATAF at execution time, see [Property Files](../configuration/properties.md) and [Runtime Credentials](../configuration/credentials.md).
