Feature: Full Client CRUD Flow with Login and Logout

  @data-driven
  Scenario Outline: Open app, login, add client, view, update, delete, and logout
    Given the user opens the application
    When the user logs in using Excel data for "<TestCaseId>"
    When the user navigates to the dashboard table
    When the user adds a client using Excel data for "<TestCaseId>"
    Then the user can view the added client
    When the user updates the client using Excel data for "<TestCaseId>"
    Then the user can delete the client and verify it is removed
    And the user logs out

    Examples:
      | TestCaseId |
      | TC_01      |
      | TC_02      |
 

