# Runners and Running Tests

ATAF ships with three preconfigured test runners. You can extend one of them or build your own using the framework's lifecycle utilities.

## Preconfigured Runners

- `BasicJUnitRunner` — for JUnit.
- `BasicTestNGRunner` — for TestNG.
- `ParallelTestNGRunner` — for parallel Cucumber scenario execution with TestNG.

To use one, simply extend it:

```java
import ataf.core.runner.BasicTestNGRunner;

public class TestRunner extends BasicTestNGRunner {

}
```

## Custom Runner

You can also build your own runner by providing the required lifecycle methods through `RunnerUtils`:

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

## Running Tests

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

Pass runtime credentials and test data secrets via system properties — see [Runtime Credentials](../configuration/credentials.md).
