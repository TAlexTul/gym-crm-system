Feature: Trainer Workload Summary API

  Scenario: Successfully fetch monthly training summary
    Given the trainer workload API is available
    When I send a valid request with trainerUsername "Jane.Jenkins", trainerFirstName "Jane", trainerLastName "Jenkins", trainerStatus "ACTIVE" and yearMonth "2024-12"
    Then I should receive a response with status "200"
    And the response should contain the summary

  Scenario: Fail to fetch summary due to invalid input
    Given the trainer workload API is available
    When I send a request with invalid data
    Then I should receive a response with status "400"
    And the response should contain an error message

  Scenario: Fail to fetch summary due to missing trainer in database
    Given the trainer workload API is available
    When I send a request with trainer data that does not exist in the database
    Then I should receive a response with status "404"
    And the response should contain an error message