Feature: Authentication API

  Scenario: Successful login
    Given a user with username "defaultadmin" and password "qwertyuiop"
    When I send a POST request to the login endpoint
    Then I should receive a response from auth endpoint with status code 200
    And the response body should contain an access token, refresh token and expireIn

  Scenario: Invalid login credentials
    Given a user with username "invaliduser" and password "wrongpassword"
    When I send a POST request to the login endpoint
    Then I should receive a response from auth endpoint with status code 401

  Scenario: Refresh access token with valid refresh token
    Given a valid refresh token
    When I send a POST request to the refresh endpoint
    Then I should receive a response from auth endpoint with status code 200
    And the response body should contain a new access token, refresh token and expireIn

  Scenario: Refresh access token with invalid refresh token
    Given a invalid refresh token
    When I send a POST request to the refresh endpoint
    Then I should receive a response from auth endpoint with status code 401

  Scenario: Invalidate refresh token with authorized default admin
    Given a valid refresh token and username "defaultadmin"
    When I send a POST request to the invalidate endpoint
    Then I should receive a response from auth endpoint with status code 204

  Scenario: Invalidate refresh token with authorized default admin and invalid refresh token
    Given a invalid refresh token and username "defaultadmin"
    When I send a POST request to the invalidate endpoint
    Then I should receive a response from auth endpoint with status code 400

  Scenario: Invalidate refresh token with unauthorized default admin
    Given a valid refresh token and unauthorized default admin
    When I send a POST request to the invalidate endpoint
    Then I should receive a response from auth endpoint with status code 401