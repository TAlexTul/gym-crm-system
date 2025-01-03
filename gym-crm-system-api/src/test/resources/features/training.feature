Feature: Training API
Scenarios for managing training data, including creating, retrieving, filtering, and deleting trainings.

  Background:
    Given the application is running and a default admin with username "defaultadmin" and password "qwertyuiop" is authenticated

  Scenario: Register a new training with a authenticated default admin
    Given a training save request with trainee "Trainee.Training1Register", trainer "Trainer.Training1Register", training name "Training1", training date "2025-01-12T23:59:59Z" and training duration "30000"
    When I send a POST request to the training endpoint
    Then I should receive a response from training endpoint with status code 201
    And the response body should contain the training ID

  Scenario: Register a new training with a authenticated default admin and trainee username is wrong
    Given a training save request with trainee "", trainer "Trainer.Training1RegisterTrainee400", training name "Training1", training date "2025-01-12T23:59:59Z" and training duration "30000"
    When I send a POST request to the training endpoint
    Then I should receive a response from training endpoint with status code 400

  Scenario: Register a new training with a authenticated default admin and trainer username is wrong
    Given a training save request with trainee "Trainer.Training1RegisterTrainer400", trainer "", training name "Training1", training date "2025-01-12T23:59:59Z" and training duration "30000"
    When I send a POST request to the training endpoint
    Then I should receive a response from training endpoint with status code 400

  Scenario: Register a new training with a authenticated default admin and training name is wrong
    Given a training save request with trainee "Trainer.Training1RegisterTraining400", trainer "Trainer.Training1RegisterTraining400", training name "", training date "2025-01-12T23:59:59Z" and training duration "30000"
    When I send a POST request to the training endpoint
    Then I should receive a response from training endpoint with status code 400

  Scenario: Register a new training with a authenticated default admin and training date is wrong
    Given a training save request with trainee "Trainer.Training1RegisterDate400", trainer "Trainer.Training1RegisterDate400", training name "Training1", training date "" and training duration "30000"
    When I send a POST request to the training endpoint
    Then I should receive a response from training endpoint with status code 400

  Scenario: Register a new training with a authenticated default admin and training duration is wrong
    Given a training save request with trainee "Trainer.Training1RegisterDuration400", trainer "Trainer.Training1RegisterDuration400", training name "Training1", training date "2025-01-12T23:59:59Z" and training duration "0"
    When I send a POST request to the training endpoint
    Then I should receive a response from training endpoint with status code 400

  Scenario: Register a new training with a unauthenticated default admin
    When I send a "POST" request to training endpoint ""
    Then I should receive a response from training endpoint with status code 401

  Scenario: List all trainings with a authenticated default admin
    Given a training save request with trainee "Trainee.Training2GetAll", trainer "Trainer.Training2GetAll", training name "Training2", training date "2025-01-12T23:59:59Z" and training duration "30000"
    When I send a GET request to the training endpoint
    Then I should receive a response from training endpoint with status code 200
    And the response body should contain a paginated list of trainings

  Scenario: List all trainings with a unauthenticated default admin
    When I send a "GET" request to training endpoint ""
    Then I should receive a response from training endpoint with status code 401

  Scenario: Filter trainings by trainer and date with a authenticated default admin
    Given a training save request with trainee "Trainee.Training3Filter", trainer "Trainer.Training3Filter", training name "Training3", training date "2025-01-12T23:59:59Z" and training duration "30000"
    When I send a GET request to the filter endpoint with trainee "Trainee.Training3Filter", trainer "Trainer.Training3Filter", from date "2025-01-11T23:59:59Z", to date "2025-01-15T23:59:59Z", training type "STRENGTH_TRAINING", page number "0" and size "5"
    Then I should receive a response from training endpoint with status code 200
    And the response body should contain a paginated list of trainings

  Scenario: Filter trainings by trainer and date with a unauthenticated default admin
    When I send a "GET" request to training endpoint "/filter"
    Then I should receive a response from training endpoint with status code 401

  Scenario: Get training by ID with a authenticated default admin
    Given a training save request with trainee "Trainee.Training4GetById", trainer "Trainer.Training4GetById", training name "Training4", training date "2025-01-12T23:59:59Z" and training duration "30000"
    When I send a GET request to the training endpoint with ID
    Then I should receive a response from training endpoint with status code 200
    And the response body should contain the training details for "Training4"

  Scenario: Get training by ID with a unauthenticated default admin
    When I send a "GET/{id}" request to training endpoint "/100"
    Then I should receive a response from training endpoint with status code 401

  Scenario: Get training by ID with a authenticated default admin
    When I send a GET request to the training endpoint with ID
    Then I should receive a response from training endpoint with status code 404

  Scenario: Delete training by ID with a authenticated default admin
    Given a training save request with trainee "Trainee.Training5Delete", trainer "Trainer.Training5Delete", training name "Training5", training date "2025-01-12T23:59:59Z" and training duration "30000"
    When I send a DELETE request to the training endpoint
    Then I should receive a response from training endpoint with status code 204
    And the training should no longer exist

  Scenario: Delete training by ID with a unauthenticated default admin
    When I send a "DELETE" request to training endpoint "/100"
    Then I should receive a response from training endpoint with status code 401

  Scenario: Delete doest not exist training by ID with a authenticated default admin
    When I send a DELETE request to the training endpoint
    Then I should receive a response from training endpoint with status code 404