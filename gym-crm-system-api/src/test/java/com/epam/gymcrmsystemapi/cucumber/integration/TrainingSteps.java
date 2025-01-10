package com.epam.gymcrmsystemapi.cucumber.integration;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.auth.request.SignInRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeSaveRequest;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerSaveRequest;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.type.Type;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.OffsetDateTime;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class TrainingSteps {

    private Response response;
    private HttpHeaders httpHeaders;
    private String id;

    @Given("the application is running and a default admin with username {string} and password {string} is authenticated")
    public void theApplicationIsRunning(String username, String password) {
        response = authenticate(username, password);
        httpHeaders = getHttpHeaders();
    }

    @Given("a training save request with trainee {string}, trainer {string}, training name {string}, " +
            "training date {string} and training duration {string}")
    public void aValidTrainingSaveRequest(String traineeUsername, String trainerUsername, String trainingName,
                                          String trainingDate, String trainingDuration) {
        response = saveTrainingSaveRequestToDB(
                traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration);
    }

    @When("I send a POST request to the training endpoint")
    public void iSendAPostRequestToTheTrainingEndpoint() {
        assertNotNull(response, "Request body must be set before sending a POST request.");
    }

    @When("I send a {string} request to training endpoint {string}")
    public void iSendAPostRequestToTheTrainingEndpoint(String method, String path) {
        httpHeaders = new HttpHeaders();
        path = path.isBlank() ? Routes.TRAININGS : Routes.TRAININGS + path;
        response = switch (method) {
            case "POST" -> saveTrainingSaveRequest(null, null, null, null, null);
            case "GET" -> sendHttpGetRequest();
            case "GET/{id}" -> sendHttpGetRequest(path);
            case "DELETE" -> sendHttpDeleteRequest(path);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
    }

    @Then("I should receive a response from training endpoint with status code {int}")
    public void iShouldReceiveAResponseWithStatusCode(int statusCode) {
        assertEquals(statusCode, response.getStatusCode(), "Response status code mismatch.");
    }

    @And("the response body should contain the training ID")
    public void theResponseBodyShouldContainTheTrainingID() {
        var jsonResponse = response.jsonPath();
        jsonResponse.getLong("id");
    }

    @When("I send a GET request to the training endpoint")
    public void iSendAGetRequestToTheTrainingEndpoint() {
        response = sendHttpGetRequest();
    }

    @When("I send a GET request to the filter endpoint with trainee {string}, trainer {string}, " +
            "from date {string}, to date {string}, training type {string}, page number {string} and size {string}")
    public void iSendAGetRequestToTheTrainingEndpointWith(String traineeUsername, String trainerUsername,
                                                          String fromDate, String toDate, String trainingType,
                                                          String pageNumber, String size) {
        response = sendHttpGetRequest(
                traineeUsername, trainerUsername, fromDate, toDate, trainingType, pageNumber, size);
    }

    @When("I send a GET request to the training endpoint with ID")
    public void iSendAGetRequestToTheTrainingEndpointWithId() {
        id = String.valueOf(Long.MAX_VALUE);
        String locationHeader = response.getHeader("Location");
        if (locationHeader != null) id = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

        response = sendHttpGetRequest(id);
    }

    @And("the response body should contain a paginated list of trainings")
    public void theResponseBodyShouldContainAPaginatedListOfTrainings() {
        var jsonResponse = response.jsonPath();
        assertTrue(jsonResponse.getList("content").size() > 0, "Training list is empty.");
    }

    @And("the response body should contain the training details for {string}")
    public void theResponseBodyShouldContainTheTrainingsDetails(String trainingName) {
        response.then().body("trainingName", equalTo(trainingName));
    }

    @When("I send a DELETE request to the training endpoint")
    public void iSendADeleteRequestToTheTrainingEndpointWithID() {
        id = String.valueOf(Long.MAX_VALUE);
        String locationHeader = response.getHeader("Location");
        if (locationHeader != null) id = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

        response = sendHttpDeleteRequest(id);
    }

    @And("the training should no longer exist")
    public void theTrainingShouldNoLongerExist() {
        response = sendHttpGetRequest(id);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode(), "Training still exists");
    }

    private Response authenticate(String username, String password) {
        var request = new SignInRequest(username, password);

        return response = sendHttpPostRequest(request, Routes.TOKEN);
    }

    private Response saveTrainingSaveRequestToDB(String traineeUsername, String trainerUsername, String trainingName,
                                                 String trainingDate, String trainingDuration) {
        response = saveTraineeSaveRequest(traineeUsername);
        response = saveTrainerSaveRequest(trainerUsername);
        return saveTrainingSaveRequest(traineeUsername, trainerUsername, trainingName, trainingDate, trainingDuration);
    }

    private Response saveTrainingSaveRequest(String traineeUsername, String trainerUsername, String trainingName,
                                             String trainingDate, String trainingDuration) {
        var date =
                (trainingDate == null || trainingDate.isBlank()) ? null : OffsetDateTime.parse(trainingDate);
        var duration =
                (trainingDuration == null || trainingDuration.isBlank() || Long.parseLong(trainingDuration) < 1) ? 0
                        : Long.parseLong(trainingDuration);
        var body = new TrainingSaveRequest(
                traineeUsername, trainerUsername, trainingName, Type.STRENGTH_TRAINING, date, duration);

        return sendHttpPostRequest(httpHeaders, body, Routes.TRAININGS);
    }

    private Response saveTraineeSaveRequest(String username) {
        if (username.isBlank()) return response;
        String[] usernameParts = username.split("\\.");
        var traineeSaveRequest = new TraineeSaveRequest(
                usernameParts[0],
                usernameParts[1],
                OffsetDateTime.parse("2024-10-19T14:59:22.345Z"),
                "123 Some St."
        );
        return sendHttpPostRequest(traineeSaveRequest, Routes.TRAINEES);
    }

    private Response saveTrainerSaveRequest(String username) {
        if (username.isBlank()) return response;
        String[] usernameParts = username.split("\\.");
        var traineeSaveRequest = new TrainerSaveRequest(
                usernameParts[0],
                usernameParts[1],
                SpecializationType.PERSONAL_TRAINER
        );
        return sendHttpPostRequest(traineeSaveRequest, Routes.TRAINERS);
    }

    private Response sendHttpPostRequest(HttpHeaders httpHeaders, Object body, String path) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders)
                .body(body)
                .post(path);
    }

    private Response sendHttpPostRequest(Object body, String path) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .post(path);
    }

    private Response sendHttpGetRequest() {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders)
                .get(Routes.TRAININGS);
    }

    private Response sendHttpGetRequest(String traineeUsername, String trainerUsername, String fromDate,
                                        String toDate, String trainingType, String pageNumber, String size) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders)
                .queryParam("traineeUsername", traineeUsername)
                .queryParam("trainerUsername", trainerUsername)
                .queryParam("fromDate", fromDate)
                .queryParam("toDate", toDate)
                .queryParam("trainingType", trainingType)
                .queryParam("pageNumber", pageNumber)
                .queryParam("size", size)
                .get(Routes.TRAININGS + "/filter");
    }

    private Response sendHttpGetRequest(String id) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders)
                .get(Routes.TRAININGS + "/" + id);
    }

    private Response sendHttpDeleteRequest(String id) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders)
                .delete(Routes.TRAININGS + "/" + id);
    }

    private HttpHeaders getHttpHeaders() {
        var authToken = response.jsonPath().getString("accessToken");

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + authToken);
        return headers;
    }
}
