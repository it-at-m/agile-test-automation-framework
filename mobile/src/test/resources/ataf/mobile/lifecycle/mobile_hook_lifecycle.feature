Feature: Lifecycle of the ATAF mobile hook

  @mobile
  Scenario: A mobile scenario gets an Android session
    Then an Android session is active

  @mobile
  Scenario: A failing mobile scenario keeps its failure
    Given an Android session is active
    Then the scenario fails on purpose

  @web
  Scenario: A web scenario does not start an Android session
    Then no Android session is active

  Scenario: An untagged scenario does not start an Android session
    Then no Android session is active
