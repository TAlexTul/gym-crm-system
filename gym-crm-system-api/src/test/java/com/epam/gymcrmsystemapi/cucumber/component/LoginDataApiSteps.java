package com.epam.gymcrmsystemapi.cucumber.component;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.auth.request.SignInRequest;
import com.epam.gymcrmsystemapi.model.user.request.OverrideLoginRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class LoginDataApiSteps {

    private Response response;

    @Given("a running application with an authorized default admin for login-data endpoint")
    public void theAppIsRunningWithDefaultAdminCredentials() {
        response = authenticate("defaultadmin", "qwertyuiop");
    }

    @Given("a running application with an unauthorized default admin for login-data endpoint")
    public void theAppIsRunningWithoutDefaultAdminCredentials() {
        response = authenticate("wrongusername", "wrongusername");
    }

    @When("I send a PATCH request to {string} with the following data:")
    public void iSendPatchRequest(String path, Map<String, String> data) {
        HttpHeaders headers = getHttpHeaders();
        var request = new OverrideLoginRequest(
                data.get("username"),
                data.get("oldPassword"),
                data.get("newPassword")
        );

        response = RestAssured
                .given()
                .headers(headers)
                .body(request)
                .patch(Routes.LOGIN + path);
    }

    @Then("the response status from LoginData Controller should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        assertThat(response.getStatusCode(), equalTo(statusCode));
    }

    private Response authenticate(String username, String password) {
        var request = new SignInRequest(username, password);

        return response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .post(Routes.TOKEN);
    }

    private HttpHeaders getHttpHeaders() {
        var authToken = response.jsonPath().getString("accessToken");

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + authToken);
        return headers;
    }
}
