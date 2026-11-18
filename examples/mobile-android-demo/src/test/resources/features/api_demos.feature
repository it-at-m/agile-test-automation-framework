@mobile
Feature: ApiDemos smoke test
  Opens Appium's public ApiDemos app on an Android device, navigates to a screen,
  enters text and verifies the result.

  Scenario: Enter text into a text field
    Given the ApiDemos app is open
    When the user navigates to "Views/Controls/1. Light Theme"
    And the user enters "Hello ATAF" into the text field
    Then the text field contains "Hello ATAF"
