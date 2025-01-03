Feature: Trainee API
Scenarios to test the trainee controller endpoints.

  Scenario: Successful register of a new trainee
    Given a trainee registration request with first name "Trainee" and last name "Successful"
    When I send a POST request to trainee endpoint
    Then the response status form trainee endpoint should be 201
    And the response body should contain the trainee ID

  Scenario: Invalid registration data of new trainee, first name is empty
    Given a trainee registration request with first name "" and last name "Successful"
    When I send a POST request to trainee endpoint
    Then the response status form trainee endpoint should be 400

  Scenario: Invalid registration data of new trainee, last name is empty
    Given a trainee registration request with first name "Trainee" and last name ""
    When I send a POST request to trainee endpoint
    Then the response status form trainee endpoint should be 400

  Scenario: Retrieve a paginated list of trainees with authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainee with username "Trainee.GetAll" exists
    When I send a GET request to trainees endpoint with page number 0 and size 5
    Then the response status form trainee endpoint should be 200
    And the response should contain a list of trainees
    And the response should have a "totalElements" field

  Scenario: Retrieve a paginated list of trainees with unauthorized default admin
    Given a authorized default admin with username "wrongusername", password "wrongpassword" and a trainee with username "Trainee.GetAll401" exists
    Then the response status form trainee endpoint should be 401

  Scenario: Get current trainee information
    Given a logged-in trainee with username "Trainee.Account"
    When I send a GET request to trainee endpoint "/account"
    Then the response status form trainee endpoint should be 200
    And the response body should contain trainee details for "Trainee.Account"

  Scenario: Get trainee information by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainee with username "Trainee.GetByID" exists
    When I send a GET request to trainee get by ID endpoint
    Then the response status form trainee endpoint should be 200
    And the response body should contain trainee details for "Trainee.GetByID"

  Scenario: Get trainee information by ID with a unauthorized default admin
    Given a authorized default admin with username "wrongusername", password "wrongpassword" and a trainee with username "Trainee.GetByID401" exists
    Then the response status form trainee endpoint should be 401

  Scenario: Get does not exist trainee information by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a does not exist trainee
    When I send a GET request to trainee get by ID endpoint
    Then the response status form trainee endpoint should be 404

  Scenario: Merge trainee information by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainee with username "Trainee.MergeByID" exists
    When I send a PATCH request to trainee endpoint with username "Trainee.MergeByID", first name "Trainee", last name "MergeByIDAfter", status "ACTIVE"
    Then the response status form trainee endpoint should be 200
    And the response body should contain trainee details for last name "MergeByIDAfter"

  Scenario: Merge trainee information by ID with a authorized default admin, first name is wrong
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainee with username "Trainee.MergeByIDFirst400" exists
    When I send a PATCH request to trainee endpoint with username "Trainee.MergeByIDFirst400", first name "", last name "MergeByIDAfter", status "ACTIVE"
    Then the response status form trainee endpoint should be 400

  Scenario: Merge trainee information by ID with a authorized default admin, last name is wrong
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainee with username "Trainee.MergeByIDLast400" exists
    When I send a PATCH request to trainee endpoint with username "Trainee.MergeByIDLast400", first name "Trainee", last name "", status "ACTIVE"
    Then the response status form trainee endpoint should be 400

  Scenario: Merge trainee information by ID with a authorized default admin, status is wrong
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainee with username "Trainee.MergeByIDStatus400" exists
    When I send a PATCH request to trainee endpoint with username "Trainee.MergeByIDStatus400", first name "Trainee", last name "MergeByIDStatus400", status ""
    Then the response status form trainee endpoint should be 400

  Scenario: Merge does not exist trainee information by ID with a unauthorized default admin
    Given a authorized default admin with username "wrongusername", password "wrongpassword" and a trainee with username "Trainee.MergeByID401" exists
    Then the response status form trainee endpoint should be 401

  Scenario: Merge does not exist trainee information by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a does not exist trainee
    When I send a PATCH request to trainee endpoint with username "Trainee.MergeByIDNotFound", first name "Trainee", last name "MergeByIDNotFoundAfter", status "ACTIVE"
    Then the response status form trainee endpoint should be 404

  Scenario: Merge trainee information by username
    Given a trainee with username "Trainee.MergeByUsername" exists
    When I send a PATCH request to trainee endpoint by username with username "Trainee.MergeByUsername", first name "Trainee", last name "MergeByUsernameAfter", status "ACTIVE"
    Then the response status form trainee endpoint should be 200
    And the response body should contain trainee details for last name "MergeByUsernameAfter"

  Scenario: Merge trainee information by username, first name is wrong
    Given a trainee with username "Trainee.MergeByUsernameFirst400" exists
    When I send a PATCH request to trainee endpoint by username with username "Trainee.MergeByUsernameFirst400", first name "", last name "MergeByUsernameFirst400After", status "ACTIVE"
    Then the response status form trainee endpoint should be 400

  Scenario: Merge trainee information by username, last name is wrong
    Given a trainee with username "Trainee.MergeByUsernameLast400" exists
    When I send a PATCH request to trainee endpoint by username with username "Trainee.MergeByUsernameLast400", first name "", last name "MergeByUsernameLast400After", status "ACTIVE"
    Then the response status form trainee endpoint should be 400

  Scenario: Merge trainee information by username, status is wrong
    Given a trainee with username "Trainee.MergeByUsernameStatus400" exists
    When I send a PATCH request to trainee endpoint by username with username "Trainee.MergeByUsernameStatus400", first name "", last name "MergeByUsernameStatus400After", status ""
    Then the response status form trainee endpoint should be 400

  Scenario: Change status of an existing trainee by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainee with username "Trainee.StatusByID" exists
    When I send a PATCH request to trainee endpoint with status "SUSPENDED"
    Then the response status form trainee endpoint should be 200
    And the response should contain the updated status "SUSPENDED"

  Scenario: Change status of an existing trainee by ID with a authorized default admin, status is wrong
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainee with username "Trainee.StatusByID400" exists
    When I send a PATCH request to trainee endpoint with status ""
    Then the response status form trainee endpoint should be 400

  Scenario: Change status of does not exist trainee by ID with a authorized default admin
    Given a authorized default admin with username "wrongusername", password "wrongpassword" and a trainee with username "Trainee.StatusByID401" exists
    Then the response status form trainee endpoint should be 401

  Scenario: Change status of does not exist trainee by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a does not exist trainee
    When I send a PATCH request to trainee endpoint with status "SUSPENDED"
    Then the response status form trainee endpoint should be 404

  Scenario: Change status of an existing trainee by username
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainee with username "Trainee.StatusByUsername" exists
    When I send a PATCH request to trainee endpoint by username "Trainee.StatusByUsername" with status "SUSPENDED"
    Then the response status form trainee endpoint should be 200
    And the response should contain the updated status "SUSPENDED"

  Scenario: Change status of an existing trainee by username, status is wrong
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainee with username "Trainee.StatusByUsername400" exists
    When I send a PATCH request to trainee endpoint by username "Trainee.StatusByUsername" with status ""
    Then the response status form trainee endpoint should be 400

  Scenario: Change status of an existing trainee by username, status is wrong
    Given a authorized default admin with username "wrongusername", password "wrongpassword" and a trainee with username "Trainee.StatusByUsername401" exists
    Then the response status form trainee endpoint should be 401

  Scenario: Change status of doest not exist trainee by username
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a does not exist trainee
    When I send a PATCH request to trainee endpoint by username "Trainee.StatusByUsernameNotFound" with status "SUSPENDED"
    Then the response status form trainee endpoint should be 404

  Scenario: Change trainee trainers set with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainee with username "Trainee.TrainersSet" exists, a trainer with username "Trainer.TrainersSet" exists
    When I send a PATCH request to "/change" endpoint with trainee username "Trainee.TrainersSet" and trainer username "Trainer.TrainersSet"
    Then the response status form trainee endpoint should be 200
    And the response should contain a changed list of trainers

  Scenario: Change trainee trainers set with an invalid trainee username and a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainee with username "Trainee.TrainersSetBedRequest" exists, a trainer with username "Trainer.TrainersSetBedRequest" exists
    When I send a PATCH request to "/change" endpoint with trainee username "" and trainer username "Trainer.TrainersSet"
    Then the response status form trainee endpoint should be 400

  Scenario: Change trainee trainers set with a unauthorized default admin
    Given a authorized default admin with username "wrongusername", password "wrongpassword" and a trainee with username "Trainee.TrainersSetBedRequest401" exists, a trainer with username "Trainer.TrainersSetBedRequest401" exists
    Then the response status form trainee endpoint should be 401

  Scenario: Delete a current trainee
    Given a trainee with username "Trainee.DeleteCurrent" exists
    When I send a DELETE request to trainee delete endpoint "/account"
    Then the response status form trainee endpoint should be 204

  Scenario: Delete a trainee by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a trainee with username "Trainee.DeleteByID" exists
    When I send a DELETE request to trainee delete by ID endpoint
    Then the response status form trainee endpoint should be 204
    And the trainee should no longer exist, check from the default admin with username "defaultadmin", password "qwertyuiop"

  Scenario: Delete a does not exist trainee by ID with a unauthorized default admin
    Given a authorized default admin with username "wrongusername", password "wrongpassword" and a trainee with username "Trainee.DeleteByID401" exists
    Then the response status form trainee endpoint should be 401

  Scenario: Delete a does not exist trainee by ID with a authorized default admin
    Given a authorized default admin with username "defaultadmin", password "qwertyuiop" and a does not exist trainee
    When I send a DELETE request to trainee delete by ID endpoint
    Then the response status form trainee endpoint should be 404