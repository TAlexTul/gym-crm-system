Feature: Health API
  As a user, I want to check if the remote API is running so that I can ensure the service is healthy.

  Scenario: Check if the health endpoint returns the expected message with an authorized default admin
    Given a running application with an authorized default admin for health endpoint
    When I send a GET request to the health endpoint
    Then I should receive a response from health endpoint with status code 200
    And the response body should contain "Remote API is running"

  Scenario: Check if the health endpoint returns the expected message with an unauthorized default admin
    Given a running application with an unauthorized default admin for health endpoint
    Then I should receive a response from health endpoint with status code 401