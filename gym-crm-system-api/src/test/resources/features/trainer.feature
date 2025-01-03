Feature: Trainer API
Scenarios to test the trainer controller endpoints.

  Scenario: Successful register of a new trainer
    Given a trainer registration request with first name "Trainer" and last name "Successful", specialization type "PERSONAL_TRAINER"
    When I send a POST request to trainer endpoint
    Then the response status form trainer endpoint should be 201
    And the response body should contain the trainer ID

  Scenario: Invalid registration data of new trainer, first name is empty
    Given a trainer registration request with first name "" and last name "Successful", specialization type "PERSONAL_TRAINER"
    When I send a POST request to trainer endpoint
    Then the response status form trainer endpoint should be 400

  Scenario: Invalid registration data of new trainer, last name is empty
    Given a trainer registration request with first name "Trainer" and last name "", specialization type "PERSONAL_TRAINER"
    When I send a POST request to trainer endpoint
    Then the response status form trainer endpoint should be 400

  Scenario: Invalid registration data of new trainer, specialization type is empty
    Given a trainer registration request with first name "Trainer" and last name "Successful", specialization type ""
    When I send a POST request to trainer endpoint
    Then the response status form trainer endpoint should be 400

  Scenario: Retrieve a paginated list of trainers with authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainer with username "Trainer.GetAll" exists
    When I send a GET request to trainers endpoint with page number 0 and size 5
    Then the response status form trainer endpoint should be 200
    And the response should contain a list of trainers
    And the response form trainer should have a "totalElements" field

  Scenario: Retrieve a paginated list of trainers with unauthorized default admin
    Given a authorized default admin with username "wrongusername", password "wrongpassword" and a trainer with username "Trainer.GetAll401" exists
    Then the response status form trainer endpoint should be 401

  Scenario: Retrieve a list of trainers not assigned by trainee username with authorized default admin // todo
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainer with username "Trainer.NotAssigned" exists and a trainee with username "Trainee.NotAssigned" exists
    Then I send a GET request to trainer endpoint "/not-assigned" with request param "Trainee.NotAssigned"
    Then the response status form trainer endpoint should be 200
    And the response should contain a list of trainers

  Scenario: Get current trainer information
    Given a logged-in trainer with username "Trainer.Account"
    When I send a GET request to trainer endpoint "/account"
    Then the response status form trainer endpoint should be 200
    And the response body should contain trainer details for "Trainer.Account"

  Scenario: Get trainer information by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainer with username "Trainer.GetByID" exists
    When I send a GET request to trainer get by ID endpoint
    Then the response status form trainer endpoint should be 200
    And the response body should contain trainer details for "Trainer.GetByID"

  Scenario: Get trainer information by ID with a unauthorized default admin
    Given a authorized default admin with username "wrongusername", password "wrongpassword" and a trainer with username "Trainer.GetByID401" exists
    Then the response status form trainer endpoint should be 401

  Scenario: Get does not exist trainer information by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a does not exist trainer
    When I send a GET request to trainer get by ID endpoint
    Then the response status form trainer endpoint should be 404

  Scenario: Merge trainer information by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainer with username "Trainer.MergeByID" exists
    When I send a PATCH request to trainer endpoint with username "Trainer.MergeByID", first name "Trainer", last name "MergeByIDAfter", status "ACTIVE", specialization type "PERSONAL_TRAINER"
    Then the response status form trainer endpoint should be 200
    And the response body should contain trainer details for last name "MergeByIDAfter"

  Scenario: Merge trainer information by ID with a authorized default admin, first name is wrong
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainer with username "Trainer.MergeByIDFirst400" exists
    When I send a PATCH request to trainer endpoint with username "Trainer.MergeByIDFirst400", first name "", last name "MergeByIDAfter", status "ACTIVE", specialization type "PERSONAL_TRAINER"
    Then the response status form trainer endpoint should be 400

  Scenario: Merge trainer information by ID with a authorized default admin, last name is wrong
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainer with username "Trainer.MergeByIDLast400" exists
    When I send a PATCH request to trainer endpoint with username "Trainer.MergeByIDLast400", first name "Trainer", last name "", status "ACTIVE", specialization type "PERSONAL_TRAINER"
    Then the response status form trainer endpoint should be 400

  Scenario: Merge trainer information by ID with a authorized default admin, status is wrong
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainer with username "Trainer.MergeByIDStatus400" exists
    When I send a PATCH request to trainer endpoint with username "Trainer.MergeByIDStatus400", first name "Trainer", last name "MergeByIDStatus400", status "", specialization type "PERSONAL_TRAINER"
    Then the response status form trainer endpoint should be 400

  Scenario: Merge trainer information by ID with a authorized default admin, specialization type is wrong
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainer with username "Trainer.MergeByIDSpec400" exists
    When I send a PATCH request to trainer endpoint with username "Trainer.MergeByIDSpec400", first name "Trainer", last name "MergeByIDSpec400", status "ACTIVE", specialization type ""
    Then the response status form trainer endpoint should be 400

  Scenario: Merge does not exist trainer information by ID with a unauthorized default admin
    Given a authorized default admin with username "wrongusername", password "wrongpassword" and a trainer with username "Trainer.MergeByID401" exists
    Then the response status form trainer endpoint should be 401

  Scenario: Merge does not exist trainer information by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a does not exist trainer
    When I send a PATCH request to trainer endpoint with username "Trainer.MergeByIDNotFound", first name "Trainer", last name "MergeByIDNotFoundAfter", status "ACTIVE", specialization type "PERSONAL_TRAINER"
    Then the response status form trainer endpoint should be 404

  Scenario: Merge trainer information by username
    Given a trainer with username "Trainer.MergeByUsername" exists
    When I send a PATCH request to trainer endpoint by username with username "Trainer.MergeByUsername", first name "Trainer", last name "MergeByUsernameAfter", status "ACTIVE", specialization type "PERSONAL_TRAINER"
    Then the response status form trainer endpoint should be 200
    And the response body should contain trainer details for last name "MergeByUsernameAfter"

  Scenario: Merge trainer information by username, first name is wrong
    Given a trainer with username "Trainer.MergeByUsernameFirst400" exists
    When I send a PATCH request to trainer endpoint by username with username "Trainer.MergeByUsernameFirst400", first name "", last name "MergeByUsernameFirst400After", status "ACTIVE", specialization type "PERSONAL_TRAINER"
    Then the response status form trainer endpoint should be 400

  Scenario: Merge trainer information by username, last name is wrong
    Given a trainer with username "Trainer.MergeByUsernameLast400" exists
    When I send a PATCH request to trainer endpoint by username with username "Trainer.MergeByUsernameLast400", first name "Trainer", last name "", status "ACTIVE", specialization type "PERSONAL_TRAINER"
    Then the response status form trainer endpoint should be 400

  Scenario: Merge trainer information by username, status is wrong
    Given a trainer with username "Trainer.MergeByUsernameStatus400" exists
    When I send a PATCH request to trainer endpoint by username with username "Trainer.MergeByUsernameStatus400", first name "Trainer", last name "MergeByUsernameStatus400After", status "", specialization type "PERSONAL_TRAINER"
    Then the response status form trainer endpoint should be 400

  Scenario: Merge trainer information by username, specialization type is wrong
    Given a trainer with username "Trainer.MergeByUsernameSpec400" exists
    When I send a PATCH request to trainer endpoint by username with username "Trainer.MergeByUsernameSpec400", first name "Trainer", last name "MergeByUsernameSpec400After", status "ACTIVE", specialization type ""
    Then the response status form trainer endpoint should be 400

  Scenario: Change status of an existing trainer by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainer with username "Trainer.StatusByID" exists
    When I send a PATCH request to trainer endpoint with status "SUSPENDED"
    Then the response status form trainer endpoint should be 200
    And the response form trainer should contain the updated status "SUSPENDED"

  Scenario: Change status of an existing trainer by ID with a authorized default admin, status is wrong
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainer with username "Trainer.StatusByID400" exists
    When I send a PATCH request to trainer endpoint with status ""
    Then the response status form trainer endpoint should be 400

  Scenario: Change status of does not exist trainer by ID with a authorized default admin
    Given a authorized default admin with username "wrongusername", password "wrongpassword" and a trainer with username "Trainer.StatusByID401" exists
    Then the response status form trainer endpoint should be 401

  Scenario: Change status of does not exist trainer by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a does not exist trainer
    When I send a PATCH request to trainer endpoint with status "SUSPENDED"
    Then the response status form trainer endpoint should be 404

  Scenario: Change status of an existing trainer by username
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainer with username "Trainer.StatusByUsername" exists
    When I send a PATCH request to trainer endpoint by username "Trainer.StatusByUsername" with status "SUSPENDED"
    Then the response status form trainer endpoint should be 200
    And the response form trainer should contain the updated status "SUSPENDED"

  Scenario: Change status of an existing trainer by username, status is wrong
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainer with username "Trainer.StatusByUsername400" exists
    When I send a PATCH request to trainer endpoint by username "Trainer.StatusByUsername" with status ""
    Then the response status form trainer endpoint should be 400

  Scenario: Change status of an existing trainer by username, status is wrong
    Given a authorized default admin with username "wrongusername", password "wrongpassword" and a trainer with username "Trainer.StatusByUsername401" exists
    Then the response status form trainer endpoint should be 401

  Scenario: Change status of doest not exist trainer by username
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a does not exist trainer
    When I send a PATCH request to trainer endpoint by username "Trainer.StatusByUsernameNotFound" with status "SUSPENDED"
    Then the response status form trainer endpoint should be 404

  Scenario: Delete a current trainer
    Given a trainer with username "Trainer.DeleteCurrent" exists
    When I send a DELETE request to trainer delete endpoint "/account"
    Then the response status form trainer endpoint should be 204

  Scenario: Delete a trainer by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainer with username "Trainer.DeleteByID" exists
    When I send a DELETE request to trainer delete by ID endpoint
    Then the response status form trainer endpoint should be 204
    And the trainer should no longer exist, check from the default admin with username "defaultadmin", password "qwertyuiop"

  Scenario: Delete a does not exist trainer by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a does not exist trainer
    When I send a DELETE request to trainer delete by ID endpoint
    Then the response status form trainer endpoint should be 204

  Scenario: Delete a does not exist trainer by ID with a unauthorized default admin
    Given a authorized default admin with username "wrongusername", password "wrongpassword" and a trainer with username "Trainer.DeleteByID401" exists
    Then the response status form trainer endpoint should be 401