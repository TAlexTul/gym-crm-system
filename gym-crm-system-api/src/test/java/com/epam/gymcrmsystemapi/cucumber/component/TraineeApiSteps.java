package com.epam.gymcrmsystemapi.cucumber.component;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.auth.request.SignInRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeChangeTrainersSetRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeMergeRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeSaveRequest;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerSaveRequest;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TraineeApiSteps {

    private Response response;
    private TraineeSaveRequest traineeSaveRequest;
    private HttpHeaders httpHeaders;
    private String id;

    @Given("a trainee registration request with first name {string} and last name {string}")
    public void aTraineeRegistrationRequestWithValidData(String firstName, String lastName) {
        traineeSaveRequest = new TraineeSaveRequest(
                firstName,
                lastName,
                OffsetDateTime.parse("2024-10-19T14:59:22.345Z"),
                "123 Test St."
        );
    }

    @Given("a authorized default admin with username {string}, password {string} " +
            "and a trainee with username {string} exists")
    public void aTraineeWithUsernameExists(String adminUsername, String adminPassword, String username) {
        response = saveTraineeSaveRequestToDB(username);

        String locationHeader = response.getHeader("Location");
        id = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

        response = authenticate(adminUsername, adminPassword);
    }

    @Given("a authorized default admin with username {string}, password {string} " +
            "and a does not exist trainee")
    public void aTraineeWithUsernameNotExists(String adminUsername, String adminPassword) {
        id = "100";
        response = authenticate(adminUsername, adminPassword);
    }

    @Given("a authorized default admin with username {string}, password {string} " +
            "and a trainee with username {string} exists, a trainer with username {string} exists")
    public void aTraineeWithUsernameExists(String adminUsername, String adminPassword,
                                           String traineeUsername, String trainerUsername) {
        response = saveTraineeSaveRequestToDB(traineeUsername);

        String[] trainerUsernameParts = trainerUsername.split("\\.");
        TrainerSaveRequest trainerSaveRequest = new TrainerSaveRequest(
                trainerUsernameParts[0],
                trainerUsernameParts[1],
                SpecializationType.PERSONAL_TRAINER
        );
        response = sendHttpPostRequest(trainerSaveRequest, Routes.TRAINERS);

        response = authenticate(adminUsername, adminPassword);
    }

    @Given("a logged-in trainee with username {string}")
    public void aLoggedInTraineeWithUsername(String username) {
        response = saveTraineeSaveRequestToDB(username);
    }

    @Given("a trainee with username {string} exists")
    public void aTraineeWithUsernameExists(String username) {
        response = saveTraineeSaveRequestToDB(username);

        var userName = response.jsonPath().getString("username");
        var password = response.jsonPath().getString("password");
        response = authenticate(userName, password);
    }

    @When("I send a POST request to trainee endpoint")
    public void iSendAPostRequestToTrainees() {
        response = sendHttpPostRequest(traineeSaveRequest, Routes.TRAINEES);
    }

    @When("I send a GET request to trainees endpoint with page number {long} and size {long}")
    public void iSendAGetRequestToAllTrainees(long page, long size) {
        httpHeaders = getHttpHeaders();
        response = RestAssured.given()
                .queryParam("page", page)
                .queryParam("size", size)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders)
                .get(Routes.TRAINEES);
    }

    @When("I send a GET request to trainee endpoint {string}")
    public void iSendAGetRequestToTraineesAccount(String endpoint) {
        var username = response.jsonPath().getString("username");
        var password = response.jsonPath().getString("password");
        response = authenticate(username, password);

        httpHeaders = getHttpHeaders();
        response = sendHttpGetRequest(httpHeaders, Routes.TRAINEES + endpoint);
    }

    @When("I send a GET request to trainee get by ID endpoint")
    public void iSendAGetByIdRequestToTraineesAccount() {
        httpHeaders = getHttpHeaders();
        response = sendHttpGetRequest(httpHeaders, Routes.TRAINEES + "/" + id);
    }

    @When("I send a PATCH request to trainee endpoint with username {string}, " +
            "first name {string}, last name {string}, status {string}")
    public void iSendAPatchRequestToTraineeByIdWithLastName(String username, String firstName,
                                                     String lastName, String status) {
        httpHeaders = getHttpHeaders();
        response = mergeTraineeMergeRequest(
                httpHeaders, username, firstName, lastName, status, Routes.TRAINEES + "/" + id);
    }

    @When("I send a PATCH request to trainee endpoint by username with username {string}, " +
            "first name {string}, last name {string}, status {string}")
    public void iSendAPatchRequestToTraineeByUsernameWithLastName(String username, String firstName,
                                                           String lastName, String status) {
        httpHeaders = getHttpHeaders();
        response = mergeTraineeMergeRequest(
                httpHeaders, username, firstName, lastName, status, Routes.TRAINEES + "/account");
    }

    @When("I send a PATCH request to trainee endpoint with status {string}")
    public void iSendAPatchRequestToWithStatus(String status) {
        httpHeaders = getHttpHeaders();
        var userStatus = (status == null || status.isBlank()) ? null : UserStatus.valueOf(status);
        var changeUserStatusRequest = new ChangeUserStatusRequest(userStatus);
        response = sendHttpPatchRequest(httpHeaders, changeUserStatusRequest, Routes.TRAINEES + "/" + id + "/status");
    }

    @When("I send a PATCH request to trainee endpoint by username {string} with status {string}")
    public void iSendAPatchRequestToTraineeByUsernameWithStatus(String username, String status) {
        httpHeaders = getHttpHeaders();
        var userStatus = (status == null || status.isBlank()) ? null : UserStatus.valueOf(status);
        var changeUserStatusRequest = new ChangeUserStatusRequest(userStatus);

        response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders)
                .queryParam("username", username)
                .body(changeUserStatusRequest)
                .patch(Routes.TRAINEES + "/status");
    }

    @When("I send a PATCH request to {string} endpoint with trainee username {string} " +
            "and trainer username {string}")
    public void iSendAPatchRequestTo(String path, String traineeUsername, String trainerUsername) {
        httpHeaders = getHttpHeaders();
        var traineeChangeTrainersSetRequest =
                new TraineeChangeTrainersSetRequest(traineeUsername, List.of(trainerUsername));
        response = sendHttpPatchRequest(httpHeaders, traineeChangeTrainersSetRequest, Routes.TRAINEES + path);
    }

    @When("I send a DELETE request to trainee delete endpoint {string}")
    public void iSendADeleteRequestToTrainees(String path) {
        httpHeaders = getHttpHeaders();
        response = sendHttpDeleteRequest(httpHeaders, Routes.TRAINEES + path);
    }

    @When("I send a DELETE request to trainee delete by ID endpoint")
    public void iSendADeleteRequestToTrainees() {
        httpHeaders = getHttpHeaders();
        response = sendHttpDeleteRequest(httpHeaders, Routes.TRAINEES + "/" + id);
    }

    @Then("the response status form trainee endpoint should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @Then("the response body should contain the trainee ID")
    public void theResponseBodyShouldContainTheTraineeID() {
        response.then().body("id", notNullValue());
    }

    @Then("the response should contain the updated status {string}")
    public void theResponseStatusShouldContainUpdatedStatus(String status) {
        response.then().body("status", is(status));
    }

    @And("the response should contain a list of trainees")
    public void theResponseShouldContainAListOfTrainees() {
        response.then().body("content", is(not(empty())));
    }

    @And("the response should have a {string} field")
    public void theResponseShouldHaveAField(String fieldName) {
        response.then().body(fieldName, notNullValue());
    }

    @And("the response body should contain trainee details for {string}")
    public void theResponseBodyShouldContainTraineeDetailsFor(String username) {
        String[] usernameParts = username.split("\\.");
        response.then().body("firstName", equalTo(usernameParts[0]));
        response.then().body("lastName", equalTo(usernameParts[1]));
    }

    @And("the response body should contain trainee details for last name {string}")
    public void theResponseBodyShouldContainTraineeDetailsForLastName(String lastName) {
        response.then().body("lastName", equalTo(lastName));
    }

    @And("the response should contain a changed list of trainers")
    public void theResponseShouldContainAListOfTrainers() {
        response.then().body("trainees", is(not(empty())));
    }

    @And("the trainee should no longer exist, check from the default admin " +
            "with username {string}, password {string}")
    public void theTraineeWithIDShouldNoLongerExist(String username, String password) {
        response = authenticate(username, password);
        httpHeaders = getHttpHeaders();
        response = sendHttpGetRequest(httpHeaders, Routes.TRAINEES + "/" + id);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCode());
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

    private Response mergeTraineeMergeRequest(HttpHeaders httpHeaders, String username, String firstName,
                                              String lastName, String status, String path) {
        var userStatus = (status == null || status.isBlank()) ? null : UserStatus.valueOf(status);
        var traineeMergeRequest = new TraineeMergeRequest(
                username, firstName, lastName, null, null, userStatus);

        return sendHttpPatchRequest(httpHeaders, traineeMergeRequest, path);
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
