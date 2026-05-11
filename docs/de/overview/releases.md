# Releases

ATAF pflegt keinen Changelog im Repository. Sämtliche Releases werden auf GitHub sowie als Artefakte auf Maven Central veröffentlicht.

- **GitHub-Releases**: [github.com/it-at-m/agile-test-automation-framework/releases](https://github.com/it-at-m/agile-test-automation-framework/releases)
- **Maven Central**: Artefakte werden unter der Gruppe `de.muenchen.ataf` veröffentlicht.

Der aktuelle Release-Tag wird zusätzlich neben dem Seitentitel oben in der Navigation angezeigt; er wird live über die GitHub-Releases-API abgerufen.

## Maven-Central-Artefakte

Nach jedem Release können nutzende Projekte (z. B. `zmsautomation`) wie folgt abhängen:

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

Ersetze `${version.ataf}` durch die tatsächliche Version. Wird das Version-Tag weggelassen, verwendet Maven die jeweils neueste verfügbare Version – das ist **nicht empfohlen**, pinne stattdessen eine explizite Version.

## Release-Prozess

Das Repository ist so eingerichtet, dass ATAF-Artefakte über einen GitHub-Actions-Workflow auf Maven Central veröffentlicht werden:

- Workflow: [`.github/workflows/maven-release.yml`](https://github.com/it-at-m/agile-test-automation-framework/blob/main/.github/workflows/maven-release.yml)
- Action: [`it-at-m/lhm_actions` Maven-Release-Composite-Action](https://github.com/it-at-m/lhm_actions/tree/main/action-templates/actions/action-maven-release)

So legst du ein Release an:

1. Öffne in GitHub den Reiter **Actions** und wähle **Release Maven**.
2. Starte den Workflow. Er:
   - verwendet das Maven-Profil `release` (Tests werden beim Release-Build übersprungen),
   - signiert und veröffentlicht Artefakte über das Sonatype-Central-Publishing-Plugin auf Maven Central,
   - öffnet einen Pull Request mit der aktualisierten Snapshot-Version (wenn `use-pr` aktiviert ist).
