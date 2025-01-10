package com.epam.gymcrmsystemapi.cucumber.component;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.auth.request.SignInRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeSaveRequest;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerMergeRequest;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerSaveRequest;
import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.model.user.request.ChangeUserStatusRequest;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainerApiSteps {

    private Response response;
    private TrainerSaveRequest trainerSaveRequest;
    private HttpHeaders httpHeaders;
    private String id;

    @Given("a trainer registration request with first name {string} and last name {string}, specialization type {string}")
    public void aTrainerRegistrationRequestWithValidData(String firstName, String lastName, String specializationType) {
        var specialization = (specializationType == null || specializationType.isBlank()) ? null
                : SpecializationType.valueOf(specializationType);
        trainerSaveRequest = new TrainerSaveRequest(
                firstName,
                lastName,
                specialization
        );
    }

    @Given("a authorized default admin with username {string}, password {string} " +
            "and a trainer with username {string} exists")
    public void aTrainerWithUsernameExists(String adminUsername, String adminPassword, String username) {
        response = saveTrainerSaveRequestToDB(username);

        String locationHeader = response.getHeader("Location");
        id = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

        response = authenticate(adminUsername, adminPassword);
    }

    @Given("a authorized default admin with username {string}, password {string} " +
            "and a trainer with username {string} exists and a trainee with username {string} exists")
    public void aTrainerWithUsernameExistsAndATraineeWithUsernameExists(String adminUsername, String adminPassword,
                                                                        String trainerUsername, String traineeUsername) {
        response = saveTrainerSaveRequestToDB(trainerUsername);
        response = saveTraineeSaveRequestToDB(traineeUsername);

        response = authenticate(adminUsername, adminPassword);
    }

    @Given("a authorized default admin with username {string}, password {string} " +
            "and a does not exist trainer")
    public void aTrainerWithUsernameNotExists(String adminUsername, String adminPassword) {
        id = "100";
        response = authenticate(adminUsername, adminPassword);
    }

    @Given("a logged-in trainer with username {string}")
    public void aLoggedInTrainerWithUsername(String username) {
        response = saveTrainerSaveRequestToDB(username);
    }

    @Given("a trainer with username {string} exists")
    public void aTrainerWithUsernameExists(String username) {
        response = saveTrainerSaveRequestToDB(username);

        var userName = response.jsonPath().getString("username");
        var password = response.jsonPath().getString("password");
        response = authenticate(userName, password);
    }

    @When("I send a POST request to trainer endpoint")
    public void iSendAPostRequestToTrainers() {
        response = sendHttpPostRequest(trainerSaveRequest, Routes.TRAINERS);
    }

    @When("I send a GET request to trainers endpoint with page number {long} and size {long}")
    public void iSendAGetRequestToAllTrainers(long page, long size) {
        httpHeaders = getHttpHeaders();
        response = RestAssured.given()
                .queryParam("page", page)
                .queryParam("size", size)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders)
                .get(Routes.TRAINERS);
    }

    @When("I send a GET request to trainer endpoint {string} with request param {string}")
    public void iSendAGetRequestToTrainersAccount(String endpoint, String requestParam) {
        httpHeaders = getHttpHeaders();
        response = sendHttpGetRequest(httpHeaders, requestParam, Routes.TRAINERS + endpoint);
    }

    @When("I send a GET request to trainer endpoint {string}")
    public void iSendAGetRequestToTrainersAccount(String endpoint) {
        var username = response.jsonPath().getString("username");
        var password = response.jsonPath().getString("password");
        response = authenticate(username, password);

        httpHeaders = getHttpHeaders();
        response = sendHttpGetRequest(httpHeaders, Routes.TRAINERS + endpoint);
    }

    @When("I send a GET request to trainer get by ID endpoint")
    public void iSendAGetByIdRequestToTrainersAccount() {
        httpHeaders = getHttpHeaders();
        response = sendHttpGetRequest(httpHeaders, Routes.TRAINERS + "/" + id);
    }

    @When("I send a PATCH request to trainer endpoint with username {string}, " +
            "first name {string}, last name {string}, status {string}, specialization type {string}")
    public void iSendAPatchRequestToTrainerByIdWithLastName(String username, String firstName,
                                                            String lastName, String status, String specializationType) {
        httpHeaders = getHttpHeaders();
        response = mergeTrainerMergeRequest(
                httpHeaders, username, firstName, lastName, status, specializationType, Routes.TRAINERS + "/" + id);
    }

    @When("I send a PATCH request to trainer endpoint by username with username {string}, " +
            "first name {string}, last name {string}, status {string}, specialization type {string}")
    public void iSendAPatchRequestToTrainerEndpointByUsernameWithLastName(String username, String firstName,
                                                                          String lastName, String status,
                                                                          String specializationType) {
        httpHeaders = getHttpHeaders();
        response = mergeTrainerMergeRequest(
                httpHeaders, username, firstName, lastName, status, specializationType, Routes.TRAINERS + "/account");
    }

    @When("I send a PATCH request to trainer endpoint with status {string}")
    public void iSendAPatchRequestToTrainerEndpointWithStatus(String status) {
        httpHeaders = getHttpHeaders();
        var userStatus = (status == null || status.isBlank()) ? null : UserStatus.valueOf(status);
        var changeUserStatusRequest = new ChangeUserStatusRequest(userStatus);
        response = sendHttpPatchRequest(httpHeaders, changeUserStatusRequest, Routes.TRAINERS + "/" + id + "/status");
    }

    @When("I send a PATCH request to trainer endpoint by username {string} with status {string}")
    public void iSendAPatchRequestToTrainerEndpointByUsernameWithStatus(String username, String status) {
        httpHeaders = getHttpHeaders();
        var userStatus = (status == null || status.isBlank()) ? null : UserStatus.valueOf(status);
        var changeUserStatusRequest = new ChangeUserStatusRequest(userStatus);

        response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders)
                .queryParam("username", username)
                .body(changeUserStatusRequest)
                .patch(Routes.TRAINERS + "/status");
    }

    @When("I send a DELETE request to trainer delete endpoint {string}")
    public void iSendADeleteRequestToTrainers(String path) {
        httpHeaders = getHttpHeaders();
        response = sendHttpDeleteRequest(httpHeaders, Routes.TRAINERS + path);
    }

    @When("I send a DELETE request to trainer delete by ID endpoint")
    public void iSendADeleteRequestToTrainers() {
        httpHeaders = getHttpHeaders();
        response = sendHttpDeleteRequest(httpHeaders, Routes.TRAINERS + "/" + id);
    }

    @Then("the response status form trainer endpoint should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @Then("the response body should contain the trainer ID")
    public void theResponseBodyShouldContainTheTrainerID() {
        response.then().body("id", notNullValue());
    }

    @Then("the response form trainer should contain the updated status {string}")
    public void theResponseFromTrainerStatusShouldContainUpdatedStatus(String status) {
        response.then().body("status", is(status));
    }

    @And("the response should contain a list of trainers")
    public void theResponseShouldContainAListOfTrainers() {
        response.then().body("content", is(not(empty())));
    }

    @And("the response form trainer should have a {string} field")
    public void theResponseShouldHaveAField(String fieldName) {
        response.then().body(fieldName, notNullValue());
    }

    @And("the response body should contain trainer details for {string}")
    public void theResponseBodyShouldContainTrainerDetailsFor(String username) {
        String[] usernameParts = username.split("\\.");
        response.then().body("firstName", equalTo(usernameParts[0]));
        response.then().body("lastName", equalTo(usernameParts[1]));
    }

    @And("the response body should contain trainer details for last name {string}")
    public void theResponseBodyShouldContainTrainerDetailsForLastName(String lastName) {
        response.then().body("lastName", equalTo(lastName));
    }

    @And("the trainer should no longer exist, check from the default admin " +
            "with username {string}, password {string}")
    public void theTrainerWithIDShouldNoLongerExist(String username, String password) {
        response = authenticate(username, password);
        httpHeaders = getHttpHeaders();
        response = sendHttpGetRequest(httpHeaders, Routes.TRAINERS + "/" + id);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
    }

    private Response saveTrainerSaveRequestToDB(String username) {
        String[] usernameParts = username.split("\\.");
        var trainerSaveRequest = new TrainerSaveRequest(
                usernameParts[0],
                usernameParts[1],
                SpecializationType.PERSONAL_TRAINER
        );
        return sendHttpPostRequest(trainerSaveRequest, Routes.TRAINERS);
    }

    private Response saveTraineeSaveRequestToDB(String username) {
        String[] usernameParts = username.split("\\.");
        var traineeSaveRequest = new TraineeSaveRequest(
                usernameParts[0],
                usernameParts[1],
                OffsetDateTime.parse("2024-10-19T14:59:22.345Z"),
                "123 Some St."
        );
        return sendHttpPostRequest(traineeSaveRequest, Routes.TRAINEES);
    }

    private Response authenticate(String username, String password) {
        var request = new SignInRequest(username, password);

        return response = sendHttpPostRequest(request, Routes.TOKEN);
    }

    private Response mergeTrainerMergeRequest(HttpHeaders httpHeaders, String username, String firstName,
                                              String lastName, String status, String specializationType, String path) {
        var userStatus = (status == null || status.isBlank()) ? null : UserStatus.valueOf(status);
        Specialization specialization = null;
        if (specializationType != null && !specializationType.isBlank()) {
            var specializationEnum = SpecializationType.valueOf(specializationType);
            specialization = new Specialization(specializationEnum, specializationEnum);
        }
        var trainerMergeRequest = new TrainerMergeRequest(username, firstName, lastName, userStatus, specialization);

        return sendHttpPatchRequest(httpHeaders, trainerMergeRequest, path);
    }

    private HttpHeaders getHttpHeaders() {
        var authToken = response.jsonPath().getString("accessToken");

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + authToken);
        return headers;
    }

    private Response sendHttpPostRequest(Object body, String path) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .post(path);
    }

    private Response sendHttpGetRequest(HttpHeaders httpHeaders, String path) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders)
                .get(path);
    }

    private Response sendHttpGetRequest(HttpHeaders httpHeaders, String username, String path) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders)
                .queryParam("username", username)
                .get(path);
    }

    private Response sendHttpPatchRequest(HttpHeaders httpHeaders,
                                          Object body, String path) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders)
                .body(body)
                .patch(path);
    }

    private Response sendHttpDeleteRequest(HttpHeaders httpHeaders, String path) {
        return RestAssured.given()
                .headers(httpHeaders)
                .delete(path);
    }
}
