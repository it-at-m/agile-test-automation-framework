# Property Files and Supported Webdrivers

Most test settings are configured through Java property files. Place them in your test resources directory, e.g. `src/test/resources`.

This page lists the four files ATAF expects and gives an annotated example for each. The driver matrix below summarises which browsers and architectures the `web` module can drive — see [`DriverUtil`](https://github.com/it-at-m/agile-test-automation-framework/blob/main/web/src/main/java/ataf/web/utils/DriverUtil.java) for the source of truth.

## Supported Webdrivers

| Browser key (`testautomation.browser`) | Selenium driver | Local execution | Selenium Grid `platformName` | Architectures | Headless |
| --- | --- | --- | --- | --- | --- |
| `chrome` | `ChromeDriver` | Windows · macOS · Linux | `LINUX` | `amd64` (Intel/x86_64), `arm64` | yes — `testautomation.browserHeadless=true` |
| `edge` | `EdgeDriver` | Windows · macOS · Linux | configurable via `testautomation.platformName` (default `WINDOWS`) | `amd64`, `arm64` | yes — `testautomation.browserHeadless=true` |
| `firefox` | `GeckoDriver` (via `FirefoxDriver`) | Windows · macOS · Linux | `LINUX` | `amd64`, `arm64` | always on, when running on grid |
| `safari` | `SafariDriver` | macOS only (10.15+) | `MAC` | `amd64` (Intel macOS/iOS), `arm64` (Apple Silicon macOS/iOS) | not supported by Safari |

Notes:

- The browser is selected with `testautomation.browser` (see [`testautomation.properties`](#testautomation-properties) below).
- For Selenium Grid runs, ATAF assigns the `platformName` capability automatically; `edge` is the only driver where you can override it via `testautomation.platformName`.
- Safari requires `testautomation.platformName=mac` and is only available on macOS/iOS hosts. See [Safari Support](#safari-support) below for full details.
- Proxy, incognito/private mode, accepting insecure certs, and eager page-load strategy are handled per browser inside `DriverUtil`.

[[toc]]

## `cucumber.properties`

Configures the behavior of Cucumber.

```properties
# Disables publishing of test reports
cucumber.publish.enabled=false

# Suppresses additional publishing output
cucumber.publish.quiet=true

# Path to the Cucumber feature files
cucumber.features=src/test/resources/features

# Generates both JSON and HTML reports
cucumber.plugin=json:target/cucumber.json,html:target/site/cucumber-pretty

# Comma-separated packages containing Cucumber step classes
cucumber.glue=test.automation.framework.steps,ataf.web.steps
```

## `jira.properties`

These properties are used for Jira integration. Some values may need to be retrieved through the Jira REST API, depending on your Jira setup.

```properties
# Project ID as a numeric value
jira.test.execution.project.id=12345678

# Summary text used for generated test executions
jira.test.execution.summary=Automatically generated test execution

# Issue type ID used for test executions
jira.test.execution.issuetype.id=-1

# Label used by the framework to identify test executions
jira.test.execution.labels.automation.label=automated

# Environment used for the test; only applies to generated test executions
jira.test.execution.test.environment=test

# Custom field ID for the test environment field in a test execution issue
jira.test.execution.test.environment.customfield.id=customfield_-1

# Custom field ID for the test plan field in a test execution issue
jira.test.execution.test.plan.customfield.id=customfield_-1

# Transition ID to move an issue to "In Progress"
jira.test.execution.transition.id.in.progress=1

# Label used by the framework to identify test executions currently in progress
jira.test.execution.labels.in.progress=inProgress

# Transition ID to move an issue to "Done"
jira.test.execution.transition.id.done=3
```

## `log4j2-test.properties`

Configures the logger behavior.

```properties
appenders=console,threadFile

appender.console.type=Console
appender.console.name=STDOUT
appender.console.layout.type=PatternLayout
appender.console.layout.pattern=[thread-id %T] %d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
appender.console.layout.charset=UTF-8

appender.threadFile.type=RollingFile
appender.threadFile.name=ThreadLog
appender.threadFile.fileName=logs/${ctx:scenario:-default}.log
appender.threadFile.filePattern=logs/${ctx:scenario:-default}-%d{yyyy-MM-dd}.log.gz
appender.threadFile.layout.type=PatternLayout
appender.threadFile.layout.pattern=[thread-id %T] %d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
appender.threadFile.layout.charset=UTF-8
appender.threadFile.policies.type=Policies
appender.threadFile.policies.time.type=TimeBasedTriggeringPolicy

rootLogger.level=info
rootLogger.appenderRefs=stdout,threadLog
rootLogger.appenderRef.stdout.ref=STDOUT
rootLogger.appenderRef.threadLog.ref=ThreadLog
```

## `testautomation.properties`

```properties
# Browser used for test automation (possible values: firefox, chrome, edge, safari)
testautomation.browser=firefox

# Target platform for the WebDriver session. Required when running Safari,
# which is only available on macOS/iOS (e.g. mac, ios).
testautomation.platformName=mac

# Browser version used for the tests
testautomation.browserVersion=140.7.0

# URL of the Selenium Grid hub used for remote test execution
testautomation.seleniumGridUrl=https://selenium.example.com/wd/hub

# Whether a proxy should be used for tests (true/false)
testautomation.boolean.useProxy=true

# Proxy host used for test connections
testautomation.proxyAddress=192.168.100.200

# Proxy port used for test connections
testautomation.int.proxyPort=8080

# Comma-separated list of domains that should bypass the proxy
testautomation.noProxy=my-example-service.example.com

# Default time in milliseconds to wait for scripts and page loads
testautomation.long.defaultScriptAndPageLoadTime=120000

# Default implicit wait time in milliseconds
testautomation.long.defaultImplicitWaitTime=250

# Default explicit wait time in seconds
testautomation.int.defaultExplicitWaitTime=60

# Whether the browser should start in incognito/private mode
testautomation.boolean.useIncognitoMode=true

# Log level for test automation
testautomation.logLevel=INFO

# Browser window width in pixels
testautomation.int.screenWidth=1920

# Browser window height in pixels
testautomation.int.screenHeight=1080

# Directory path containing Firefox extensions used for testing
testautomation.firefoxExtensionDirectory=./src/test/resources/extensions/firefox/

# Jira REST API URLs
testautomation.jiraRestApiUrl=https://jira.example.com/rest/api/2/
testautomation.jiraXrayRestApiUrl=https://jira.example.com/rest/raven/1.0/
```

### Safari Support

Since [ATAF 0.3.3](https://github.com/it-at-m/agile-test-automation-framework/pull/13), the WebDriver layer in `ataf-web` can drive Safari via `SafariDriver`/`SafariOptions`, both locally and through a Selenium Grid. This enables tests on:

- `amd64` (Intel) macOS and iOS machines.
- `arm64` (Apple Silicon) macOS and iOS machines.

To run tests against Safari, set:

```properties
testautomation.browser=safari
testautomation.platformName=mac
```

Example log output at startup:

```text
[1] 21:45:06 INFO  TestProperties:148 - (string) Property [platformName] with value [mac] has been successfully loaded!
[1] 21:45:06 INFO  TestProperties:148 - (string) Property [browser] with value [safari] has been successfully loaded!
```

::: warning Safari is macOS/iOS only
Safari is not available on `Windows amd64`/`Windows arm64` or on `amd64`/`arm64` Linux. As a result, Safari **cannot run on Linux GitHub Actions runners**. To execute Safari-based tests in CI you currently have two options:

- Use macOS GitHub Actions runners (e.g. `macos-latest` or `macos-26-intel`; see the [runner images catalog](https://github.com/actions/runner-images/blob/main/images/macos/macos-26-Readme.md#browsers)) and configure them to run Safari, or
- Route the Safari runs through a cloud-based cross-browser testing service from your GitHub Actions workflow.
  :::

For credentials that must not be committed alongside these property files, see [Runtime Credentials](./credentials.md).
