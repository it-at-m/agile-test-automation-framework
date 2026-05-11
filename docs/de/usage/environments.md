# Umgebungen und Systeme

Damit das Framework korrekt funktioniert, sollte mindestens eine Umgebung in deinem Code definiert sein. ATAF bietet zwei Möglichkeiten zur Initialisierung.

## Variante 1: Statische Initialisierung im Test-Runner

Initialisiere die Testdaten in einem statischen Block des Runners, sodass sie vor jeder Testklasse ausgeführt werden:

```java
import ataf.core.runner.BasicTestNGRunner;
import test.automation.framework.data.TestData;

public class TestRunner extends BasicTestNGRunner {
    static {
        TestData.init();
    }
}
```

`TestData`-Klasse mit Test- und Integrationsumgebungen:

```java
import ataf.core.data.Environment;
import ataf.core.logging.ScenarioLogManager;

public class TestData {

    public static final Environment TEST_ENVIRONMENT =
        new Environment("Environment", "TEST");
    public static final Environment INTEGRATION_ENVIRONMENT =
        new Environment("Environment", "INT");

    public static void init() {
        ScenarioLogManager.getLogger().info("Start of initializing test data");

        ScenarioLogManager.getLogger().info("Adding systems for TEST");
        TEST_ENVIRONMENT.addSystem("My Example System 1", "https://my-example-system-1-test.example.com/");
        TEST_ENVIRONMENT.addSystem("My Example System 2", "https://my-example-system-2-test.example.com/");
        TEST_ENVIRONMENT.addSystem("My Example System 3", "https://my-example-system-3-test.example.com/");

        ScenarioLogManager.getLogger().info("Adding systems for INT");
        INTEGRATION_ENVIRONMENT.addSystem("My Example System 1", "https://my-example-system-1-int.example.com/");
        INTEGRATION_ENVIRONMENT.addSystem("My Example System 2", "https://my-example-system-2-int.example.com/");
        INTEGRATION_ENVIRONMENT.addSystem("My Example System 3", "https://my-example-system-3-int.example.com/");

        ScenarioLogManager.getLogger().info("Finished initializing test data");
    }
}
```

Wenn deine Teststrategie keine Umgebungen nutzt – etwa weil ausschließlich gegen produktive Systeme getestet wird – kannst du auch einen leeren Wert angeben:

```java
import ataf.core.data.Environment;

public class TestData {
    public static final Environment NO_ENVIRONMENT = new Environment("Environment", "");

    public static void init() {
        // Wird für die Init-Reihenfolge der Statics benötigt
    }
}
```

## Variante 2: Annotierte Init-Methode

Du kannst auch den TestNG-Lifecycle nutzen, indem du eine `init()`-Methode mit `@BeforeSuite(alwaysRun = true, dependsOnGroups = "beforeTestSuite")` annotierst. Die Methode wird dann direkt nach `ataf.core.runner.BasicTestNGRunner.beforeTestSuite` ausgeführt. Besonders nützlich, wenn deine `TestData`-Klasse von Property-Werten abhängt.

```java
import ataf.core.data.Environment;
import ataf.core.logging.ScenarioLogManager;
import org.testng.annotations.BeforeSuite;

public class TestData {

    // Definiere hier deine Umgebungen

    @BeforeSuite(alwaysRun = true, dependsOnGroups = "beforeTestSuite")
    public void init() {
        ScenarioLogManager.getLogger().info("Start of initializing test data");

        // Initialisiere deine Umgebungen hier mit Systemen

        ScenarioLogManager.getLogger().info("Finished initializing test data");
    }
}
```
