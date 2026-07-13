# Standalone Usage (No Jira, Local Keycloak)

ATAF was built for agile teams that manage tests in Jira and Xray, but **none of that is required**. You can use the framework as a plain Cucumber + Selenium + REST Assured stack: feature files live in your Git repository, reports stay in `target/`, and UI tests authenticate against a **local Keycloak** instead of a corporate or government SSO environment.

This is how the [zmsautomation](https://it-at-m.github.io/eappointment/testing-and-automation/zmsautomation.html) test suite in the eAppointment project is set up today.

## Without Jira and Xray

Jira integration is **opt-in**. ATAF only talks to Jira when you provide credentials (`auth_token` or `username`/`password`) **and** configure a Jira workflow (for example via `jira.properties` and Surefire properties such as `issueKeys` or `filterId`). If those are missing, the runner keeps local feature files and skips Xray test-execution updates.

### Store feature files in the repository

Keep `.feature` files under `src/test/resources/features/` (or any path you set in `cucumber.properties`):

```properties
cucumber.features=src/test/resources/features
cucumber.glue=your.project.steps,ataf.web.steps
cucumber.plugin=json:target/cucumber.json,html:target/site/cucumber-pretty
```

Run tests with Maven as usual — no Jira export step runs beforehand.

### What you can omit

| Jira-based setup                                      | Standalone setup                                                                              |
| ----------------------------------------------------- | --------------------------------------------------------------------------------------------- |
| `jira.properties`                                     | Not needed                                                                                    |
| Runtime `auth_token`, `username`, `password` for Jira | Not needed ([Runtime Credentials](../configuration/credentials.md) only applies to Jira/Xray) |
| Feature files maintained in Jira/Xray                 | Feature files in Git                                                                          |
| Xray test execution updated after each run            | Cucumber HTML/JSON + Surefire reports only ([Reporting](../reporting.md))                     |

The ATAF `core` module still ships Jira/Xray helpers, but hooks such as `RunnerUtils.isJiraBasedTestExecution()` stay inactive when features are loaded from disk.

See [Writing Tests](./writing-tests.md) for Cucumber and TestNG/JUnit examples.

## Without corporate or government SSO

UI tests that log in through OpenID Connect do **not** need access to an internal `ssodev` or production IdP. The same pattern used in the [RefArch stack](https://github.com/it-at-m/refarch-templates/tree/main/stack/keycloak/migration) works for ATAF:

1. Run **Keycloak** locally (Docker Compose).
2. Apply realm configuration with **[keycloakmigration](https://github.com/klg71/keycloakmigration)** (`klg71/keycloakmigration` image).
3. Point your application and ATAF test properties at the local realm, clients, and test users.

The RefArch templates define a minimal local stack in [`stack/docker-compose.yml`](https://github.com/it-at-m/refarch-templates/blob/4735e9f425a29e9cd38eafc6cd34b5da705f0574/stack/docker-compose.yml) — Keycloak on port `8100`, an `init-keycloak` sidecar that waits for Keycloak and applies YAML migrations from `stack/keycloak/migration/`.

### Typical local Keycloak layout

```text
keycloak          # quay.io/keycloak/keycloak — start-dev, KC_HTTP_RELATIVE_PATH=/auth
init-keycloak     # klg71/keycloakmigration — applies KEYCLOAK_CHANGELOG on first start
migration/        # YAML changelog (realm, clients, roles, users)
```

Environment variables for `init-keycloak` match the RefArch example:

- `ADMIN_USER` / `ADMIN_PASSWORD` — Keycloak bootstrap admin
- `BASEURL` — e.g. `http://keycloak:8080/auth` (in-container) or `http://keycloak:8100/auth` (RefArch port)
- `WAIT_FOR_KEYCLOAK=true`
- `KEYCLOAK_CHANGELOG=/migration/keycloak-changelog.yml`

Configure ATAF UI credentials in `testautomation.properties` (or environment) to match a user from the migration, for example:

```properties
testautomation.userName=ataf
testautomation.userPassword=vorschau
```

Add internal hostnames to `testautomation.noProxy` when the browser runs behind a corporate HTTP proxy but must reach Docker services directly (see [zmsautomation](https://it-at-m.github.io/eappointment/testing-and-automation/zmsautomation.html#local-keycloak-setup) for a real example).

## Reference: zmsautomation in eAppointment

The [zmsautomation documentation](https://it-at-m.github.io/eappointment/testing-and-automation/zmsautomation.html) describes the full end-to-end setup: DDEV/devcontainer, local Keycloak migrations, Flyway test data, and ATAF profiles (`ataf-api`, `ataf-ui`).

Highlights:

- **No `jira.properties`** — all scenarios live under `zmsautomation/src/test/resources/features/`.
- **Local Keycloak** — `.resources/keycloak/migration/` (changelog-driven realm `zms`, clients, roles, user `ataf` / `vorschau` for UI tests).
- **Docker Compose** — Keycloak + `init-keycloak` in [`.ddev/docker-compose.keycloak.yaml`](https://github.com/it-at-m/eappointment/blob/main/.ddev/docker-compose.keycloak.yaml) and [`.devcontainer/docker-compose.yaml`](https://github.com/it-at-m/eappointment/blob/main/.devcontainer/docker-compose.yaml).
- **Host mapping** — add `127.0.0.1 keycloak` to `/etc/hosts` so browser redirects work ([Local Keycloak Setup](https://it-at-m.github.io/eappointment/setup-and-development/local-keycloak-setup.html) in the eAppointment docs).

If you are starting a new project, copy the RefArch migration pattern first, then wire ATAF properties to the users and redirect URIs your application expects.
