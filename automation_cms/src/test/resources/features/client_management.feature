Feature: Client Management in Financial Advisor Portal

  @smoke @data-driven
  Scenario Outline: Add a completely new client and verify in dashboard
    Given the user opens the application
    When the user logs in using Excel data for "<TestCaseId>"
    When the user navigates to the dashboard table
    When the user adds a client using Excel data for "<TestCaseId>"
    Then the user can view the added client
    And the user logs out

    Examples:
      | TestCaseId |
      | TC_01      |
      | TC_02      |

