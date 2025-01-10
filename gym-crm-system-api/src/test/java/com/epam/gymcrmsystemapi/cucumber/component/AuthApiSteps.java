package com.epam.gymcrmsystemapi.cucumber.component;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.auth.request.RefreshTokenRequest;
import com.epam.gymcrmsystemapi.model.auth.request.SignInRequest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class AuthApiSteps {

    private final Map<String, Object> requestBody = new HashMap<>();
    private Response response;

    @Given("a user with username {string} and password {string}")
    public void aUserWithUsernameAndPassword(String username, String password) {
        requestBody.put("username", username);
        requestBody.put("password", password);
    }

    @Given("a valid refresh token")
    public void aValidRefreshToken() {
        response = authenticate();

        var refreshToken = response.jsonPath().getString("refreshToken");
        requestBody.put("refreshToken", refreshToken);
    }

    @Given("a invalid refresh token")
    public void aInValidRefreshToken() {
        var refreshToken = "invalidRefreshToken";
        requestBody.put("refreshToken", refreshToken);
    }

    @Given("a valid refresh token and username {string}")
    public void aValidRefreshTokenAndUsername(String username) {
        response = authenticate();

        var accessToken = response.jsonPath().getString("accessToken");
        var refreshToken = response.jsonPath().getString("refreshToken");
        requestBody.put("accessToken", accessToken);
        requestBody.put("refreshToken", refreshToken);
        requestBody.put("username", username);
    }

    @Given("a invalid refresh token and username {string}")
    public void aNullRefreshTokenAndUsername(String username) {
        response = authenticate();

        var accessToken = response.jsonPath().getString("accessToken");
        requestBody.put("accessToken", accessToken);
        requestBody.put("refreshToken", null);
        requestBody.put("username", username);
    }

    @Given("a valid refresh token and unauthorized default admin")
    public void aValidRefreshTokenAndUnauthorizedDefaultAdmin() {
        response = authenticate();

        var refreshToken = response.jsonPath().getString("refreshToken");
        requestBody.put("accessToken", "wrongAccessToken");
        requestBody.put("refreshToken", refreshToken);
        requestBody.put("username", "wrongUsername");
    }

    @When("I send a POST request to the login endpoint")
    public void iSendAPOSTRequestToTheLoginEndpoint() {
        response = sendHttpPostRequest(requestBody, Routes.TOKEN);
    }

    @When("I send a POST request to the refresh endpoint")
    public void iSendAPOSTRequestToTheRefreshEndpoint() {
        response = sendHttpPostRequest(requestBody, Routes.TOKEN + "/refresh");
    }

    @When("I send a POST request to the invalidate endpoint")
    public void iSendAPOSTRequestToTheInvalidateEndpoint() {
        var refreshToken = (String) requestBody.get("refreshToken");
        var refreshTokenRequest = new RefreshTokenRequest(
                refreshToken
        );
        HttpHeaders headers = getHttpHeaders();

        response = RestAssured
                .given()
                .headers(headers)
                .body(refreshTokenRequest)
                .post(Routes.TOKEN + "/invalidate");
    }

    @Then("I should receive a response from auth endpoint with status code {int}")
    public void iShouldReceiveAResponseWithStatusCode(int statusCode) {
        assertThat(response.statusCode(), equalTo(statusCode));
    }

    @And("the response body should contain an access token, refresh token and expireIn")
    public void theResponseBodyShouldContainAnAccessToken() {
        assertThat(response.getBody().asString(), containsString("accessToken"));
        assertThat(response.getBody().asString(), containsString("refreshToken"));
        assertThat(response.getBody().asString(), containsString("expireIn"));
    }

    @And("the response body should contain a new access token, refresh token and expireIn")
    public void theResponseBodyShouldContainANewAccessToken() {
        assertThat(response.getBody().asString(), containsString("accessToken"));
        assertThat(response.getBody().asString(), containsString("refreshToken"));
        assertThat(response.getBody().asString(), containsString("expireIn"));
    }

    private Response authenticate() {
        var request = new SignInRequest("defaultadmin", "qwertyuiop");

        return response = sendHttpPostRequest(request, Routes.TOKEN);
    }

    private HttpHeaders getHttpHeaders() {
        var authToken = (String) requestBody.get("accessToken");

        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + authToken);
        return httpHeaders;
    }

    private Response sendHttpPostRequest(Object body, String path) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .post(path);
    }
}
