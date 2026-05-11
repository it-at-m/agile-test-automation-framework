# Releases

ATAF does not maintain an in-repository changelog. All releases are published on GitHub and as artifacts on Maven Central.

- **GitHub releases**: [github.com/it-at-m/agile-test-automation-framework/releases](https://github.com/it-at-m/agile-test-automation-framework/releases)
- **Maven Central**: artifacts are published under the `de.muenchen.ataf` group.

The latest release tag is also shown next to the site title in the top navigation; it is fetched live from the GitHub Releases API.

## Maven Central Artifacts

After each release, consumers (such as `zmsautomation`) can depend on:

```xml
<dependency>
    <groupId>de.muenchen.ataf</groupId>
    <artifactId>core</artifactId>
    <version>${version.ataf}</version>
</dependency>

<dependency>
    <groupId>de.muenchen.ataf</groupId>
    <artifactId>rest</artifactId>
    <version>${version.ataf}</version>
</dependency>

<dependency>
    <groupId>de.muenchen.ataf</groupId>
    <artifactId>web</artifactId>
    <version>${version.ataf}</version>
</dependency>
```

Replace `${version.ataf}` with the actual version. Omitting the version tag will cause Maven to use the latest available version, which is **not recommended** — pin to an explicit version.

## Release Process

The repository is configured to publish ATAF artifacts to Maven Central using a GitHub Actions workflow:

- Workflow: [`.github/workflows/maven-release.yml`](https://github.com/it-at-m/agile-test-automation-framework/blob/main/.github/workflows/maven-release.yml)
- Action: [`it-at-m/lhm_actions` Maven release composite action](https://github.com/it-at-m/lhm_actions/tree/main/action-templates/actions/action-maven-release)

To create a release:

1. Open the **Actions** tab in GitHub and select **Release Maven**.
2. Run the workflow. It will:
   - Use the Maven `release` profile (which skips tests during the release build).
   - Sign and deploy artifacts to Maven Central via the Sonatype Central publishing plugin.
   - Open a pull request with the updated snapshot version (when `use-pr` is enabled).
