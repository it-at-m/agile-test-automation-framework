# Runner und Testausführung

ATAF liefert drei vorkonfigurierte Test-Runner mit. Du kannst einen davon erben oder einen eigenen Runner über die Lifecycle-Utilities des Frameworks bauen.

## Vorkonfigurierte Runner

- `BasicJUnitRunner` – für JUnit.
- `BasicTestNGRunner` – für TestNG.
- `ParallelTestNGRunner` – für parallele Cucumber-Szenarioausführung mit TestNG.

Zum Einsatz erbst du einfach davon:

```java
import ataf.core.runner.BasicTestNGRunner;

public class TestRunner extends BasicTestNGRunner {

}
```

## Eigener Runner

Du kannst auch einen eigenen Runner bauen, indem du die nötigen Lifecycle-Methoden über `RunnerUtils` bereitstellst:

```java
import ataf.core.utils.RunnerUtils;

public class CustomTestRunner {

    public void beforeTestSuite() {
        RunnerUtils.setupTestSuite();
    }

    public void afterTestSuite() {
        RunnerUtils.tearDownTestSuite();
    }
}
```

## Tests ausführen

### Cucumber

```bash
mvn clean test -Dcucumber.filter.tags=@smoke
```

### TestNG

```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

### JUnit

```bash
mvn clean test
```

Laufzeit-Zugangsdaten und Testdaten-Geheimnisse werden als System-Properties übergeben – siehe [Laufzeit-Zugangsdaten](../configuration/credentials.md).
