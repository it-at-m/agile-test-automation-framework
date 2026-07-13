# Writing Tests

ATAF supports two complementary test styles: Cucumber-based BDD scenarios and traditional TestNG/JUnit test classes. Both can live in the same project.

## Writing Cucumber Tests

Create Cucumber scenarios either in Jira/Xray **or** directly as feature files in your repository. The latter is enough for most projects — you do not need Jira at all. See [Standalone Usage (No Jira, Local Keycloak)](./standalone-without-jira.md) for a full walkthrough; [zmsautomation](https://it-at-m.github.io/eappointment/testing-and-automation/zmsautomation.html) is a production example that stores features in Git and uses local Keycloak for SSO.

Set the correct Cucumber tags for the corresponding package, such as `@web` or `@rest`. This ensures that the correct hook class is used. In Jira, these tags can be set as labels on the test case issues; in a repository-only setup, set them in the `.feature` files.

Example feature:

```gherkin
Feature: Login functionality

  @smoke @web
  Scenario: Successful login with valid credentials
    Given the user is on the login page
    When the user enters valid credentials
    Then the user should be redirected to the dashboard
```

Each Cucumber step must then be implemented in one of your step classes:

```java
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@Given("the user is on the login page")
public void given_the_user_is_on_the_login_page() {
    // Add precondition code here
}

@When("the user enters valid credentials")
public void when_the_user_enters_valid_credentials() {
    // Add action code here
}

@Then("the user should be redirected to the dashboard")
public void then_the_user_should_be_redirected_to_the_dashboard() {
    // Add verification code here
}
```

## Writing TestNG or JUnit Tests

You can continue to create test classes independently of Cucumber under `src/test/java`:

```java
import org.testng.annotations.Test;

public class LoginTest {

    @Test
    public void testValidLogin() {
        // Your test code here
    }
}
```

## Provider Configuration

Surefire usually selects the appropriate test framework provider automatically, based on the version of TestNG or JUnit found in your project classpath. In some cases, however, you may want to override this behavior manually by adding the required provider dependency to the Surefire plugin.

Example for TestNG:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.5.5</version>
    <configuration>
        <trimStackTrace>false</trimStackTrace>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.apache.maven.surefire</groupId>
            <artifactId>surefire-testng</artifactId>
            <version>3.5.5</version>
        </dependency>
    </dependencies>
</plugin>
```
