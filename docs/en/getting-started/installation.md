# Installation

Add the following Maven dependencies to your project's `pom.xml` as needed.

Pin an explicit version such as `0.3.4`. Omitting the version tag will cause Maven to use the latest available version, which is **not recommended** (see [Releases](../overview/releases.md)).

## Core Package

Required for using ATAF. Contains essential functionality for Cucumber and Jira integration, as well as helpful classes for test data and properties.

```xml
<dependency>
    <groupId>de.muenchen.ataf</groupId>
    <artifactId>core</artifactId>
    <version>0.3.4</version>
</dependency>
```

## REST Package (optional)

Contains classes for API testing.

```xml
<dependency>
    <groupId>de.muenchen.ataf</groupId>
    <artifactId>rest</artifactId>
    <version>0.3.4</version>
</dependency>
```

## Web Package (optional)

Contains classes for browser-based tests.

```xml
<dependency>
    <groupId>de.muenchen.ataf</groupId>
    <artifactId>web</artifactId>
    <version>0.3.4</version>
</dependency>
```

## Mobile Package (optional)

Contains classes for testing native Android apps with Appium and UiAutomator2. The module is new and is published with the next release; until then, build it locally with `mvn install` and use `0.3.5-SNAPSHOT`. See [Mobile App Testing (Android)](../usage/mobile-android.md).

```xml
<dependency>
    <groupId>de.muenchen.ataf</groupId>
    <artifactId>mobile</artifactId>
    <version>0.3.5-SNAPSHOT</version>
</dependency>
```

After configuring your dependencies, continue with [Build and Integration Tests](./build.md).
