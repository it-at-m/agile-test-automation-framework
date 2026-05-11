# Environments and Systems

For the framework to work correctly, at least one environment should be defined in your code. ATAF gives you two ways to initialize them.

## Option 1: Static Initialization in the Test Runner

Initialize test data in a static block of the runner so it runs before any test class:

```java
import ataf.core.runner.BasicTestNGRunner;
import test.automation.framework.data.TestData;

public class TestRunner extends BasicTestNGRunner {
    static {
        TestData.init();
    }
}
```

A `TestData` class with test and integration environments:

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

If your test strategy does not use environments — for example because you only test against productive systems — you can also provide an empty value:

```java
import ataf.core.data.Environment;

public class TestData {
    public static final Environment NO_ENVIRONMENT = new Environment("Environment", "");

    public static void init() {
        // Required for init order of statics
    }
}
```

## Option 2: Annotated Initialization Method

You can also use the TestNG lifecycle by annotating an `init()` method with `@BeforeSuite(alwaysRun = true, dependsOnGroups = "beforeTestSuite")`. The method is then executed directly after `ataf.core.runner.BasicTestNGRunner.beforeTestSuite`. This is especially useful if your `TestData` class depends on property values.

```java
import ataf.core.data.Environment;
import ataf.core.logging.ScenarioLogManager;
import org.testng.annotations.BeforeSuite;

public class TestData {

    // Define your environments here

    @BeforeSuite(alwaysRun = true, dependsOnGroups = "beforeTestSuite")
    public void init() {
        ScenarioLogManager.getLogger().info("Start of initializing test data");

        // Initialize your environments with systems here

        ScenarioLogManager.getLogger().info("Finished initializing test data");
    }
}
```
