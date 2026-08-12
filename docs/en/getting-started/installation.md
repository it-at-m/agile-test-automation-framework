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

After configuring your dependencies, continue with [Build and Integration Tests](./build.md).
