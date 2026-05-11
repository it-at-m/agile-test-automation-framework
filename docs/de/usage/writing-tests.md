# Tests schreiben

ATAF unterstützt zwei sich ergänzende Test-Stile: Cucumber-basierte BDD-Szenarien sowie klassische TestNG-/JUnit-Testklassen. Beide können im selben Projekt nebeneinander existieren.

## Cucumber-Tests schreiben

Lege Cucumber-Szenarien entweder in Jira oder direkt als Feature-Dateien im Repository an.

Achte darauf, dass die passenden Cucumber-Tags für das jeweilige Paket gesetzt sind, etwa `@web` oder `@rest`. So wird die richtige Hook-Klasse verwendet. In Jira können diese Tags als Labels an den Testfall-Issues gepflegt werden.

Beispiel-Feature:

```gherkin
Feature: Login functionality

  @smoke @web
  Scenario: Successful login with valid credentials
    Given the user is on the login page
    When the user enters valid credentials
    Then the user should be redirected to the dashboard
```

Jeder Cucumber-Step muss in einer deiner Step-Klassen implementiert werden:

```java
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

@Given("the user is on the login page")
public void given_the_user_is_on_the_login_page() {
    // Vorbedingungs-Code hier einfügen
}

@When("the user enters valid credentials")
public void when_the_user_enters_valid_credentials() {
    // Aktions-Code hier einfügen
}

@Then("the user should be redirected to the dashboard")
public void then_the_user_should_be_redirected_to_the_dashboard() {
    // Verifizierungs-Code hier einfügen
}
```

## TestNG- oder JUnit-Tests schreiben

Du kannst weiterhin Testklassen unabhängig von Cucumber unter `src/test/java` anlegen:

```java
import org.testng.annotations.Test;

public class LoginTest {

    @Test
    public void testValidLogin() {
        // Testcode hier einfügen
    }
}
```

## Provider-Konfiguration

Surefire wählt den passenden Test-Framework-Provider üblicherweise automatisch anhand der TestNG- bzw. JUnit-Version im Projekt-Classpath. In manchen Fällen willst du dieses Verhalten manuell überschreiben und die benötigte Provider-Abhängigkeit zum Surefire-Plugin hinzufügen.

Beispiel für TestNG:

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
