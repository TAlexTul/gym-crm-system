Feature: Training Type Management
  Retrieve a list of all training types available in the application.

  Scenario: Get all training types
    Given a running application with an authorized default admin for training types endpoint
    When I retrieve all training types
    Then the response status should be 200
    And the response should contain the following training types:
      | id  | name                    |
      | 0   | STRENGTH_TRAINING       |
      | 1   | CARDIO_WORKOUT          |
      | 2   | FUNCTIONAL_TRAINING     |
      | 3   | CROSSFIT_WORKOUT        |
      | 4   | PILATES_SESSION         |
      | 5   | BODYBUILDING_PROGRAM    |
      | 6   | MARTIAL_ARTS_TRAINING   |
      | 7   | SWIMMING_SESSION        |
      | 8   | GROUP_FITNESS_CLASS     |
      | 9   | FITNESS_AEROBICS        |
      | 10  | REHABILITATION_WORKOUT  |
      | 11  | NUTRITION_AND_DIET_PLAN |
      | 12  | CYCLING_WORKOUT         |
      | 13  | GYMNASTICS_TRAINING     |
      | 14  | TRX_TRAINING            |
      | 15  | SPECIAL_NEEDS_TRAINING  |
      | 16  | STRETCHING_SESSION      |
      | 17  | BOOTCAMP_WORKOUT        |

  Scenario: Get all training types with an unauthorized default admin
    Given a running application with an unauthorized default admin for training types endpoint
    Then the response status should be 401