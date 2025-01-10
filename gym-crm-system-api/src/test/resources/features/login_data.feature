Feature: Login Data Management
  As an admin or authenticated user
  I want to update login data
  So that it reflects changes in the system

  Scenario: Update user login data by ID with an authorized default admin
    Given a running application with an authorized default admin for login-data endpoint
    When I send a PATCH request to "/1" with the following data:
      | username      | defaultadmin         |
      | oldPassword   | qwertyuiop           |
      | newPassword   | qwertyuiop           |
    Then the response status from LoginData Controller should be 200

  Scenario: Update user login data by ID with an unauthorized default admin
    Given a running application with an unauthorized default admin for login-data endpoint
    Then the response status from LoginData Controller should be 401

  Scenario: User by ID not found for updating login data
    Given a running application with an authorized default admin for login-data endpoint
    When I send a PATCH request to "/100" with the following data:
      | username      | notExistUsername      |
      | oldPassword   | qwertyuiop            |
      | newPassword   | qwertyuiopqwertyuiop  |
    Then the response status from LoginData Controller should be 404

  Scenario: Update current user login data
    Given a running application with an authorized default admin for login-data endpoint
    When I send a PATCH request to "/account" with the following data:
      | username      | defaultadmin          |
      | oldPassword   | qwertyuiop            |
      | newPassword   | qwertyuiop            |
    Then the response status from LoginData Controller should be 200