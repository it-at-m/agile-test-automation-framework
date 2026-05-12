# Reporting

After test execution, reports are generated in the `target/surefire-reports` directory. You will typically find:

- **HTML report** — a detailed overview of the test execution.
- **JUnit XML report** — XML format for integration into CI tools.

If Jira integration is enabled, the corresponding Jira Xray test execution is also updated directly with the test results.

## Cucumber Reports

When using Cucumber with the default plugin configuration shown in [Property Files → `cucumber.properties`](./configuration/properties.md#cucumber-properties), the following reports are generated:

- `target/cucumber.json` — JSON report (machine-readable; ingest into other tooling).
- `target/site/cucumber-pretty` — HTML report.

## CI Integration

JUnit XML output makes the reports compatible with most CI dashboards out of the box:

- GitHub Actions: upload `target/surefire-reports/**/*.xml` with [test-reporter](https://github.com/dorny/test-reporter) or any equivalent action.
- GitLab CI: use the [`junit:` artifact reports](https://docs.gitlab.com/ci/yaml/artifacts_reports/#artifactsreportsjunit) field.
- Jenkins: use the JUnit plugin.
