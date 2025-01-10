package com.epam.gymcrmsystemapi.cucumber.component;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.auth.request.SignInRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthApiSteps {

    private Response response;

    @Given("a running application with an authorized default admin for health endpoint")
    public void theAppIsRunningWithDefaultAdminCredentials() {
        response = authenticate("defaultadmin", "qwertyuiop");
    }

    @Given("a running application with an unauthorized default admin for health endpoint")
    public void theAppIsRunningWithoutDefaultAdminCredentials() {
        response = authenticate("wrongusername", "wrongpassword");
    }

    @When("I send a GET request to the health endpoint")
    public void iSendAGETRequestToTheHealthEndpoint() {
        HttpHeaders httpHeaders = getHttpHeaders();

        response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders)
                .get(Routes.HEALTH_API);
    }

    @Then("I should receive a response from health endpoint with status code {int}")
    public void iShouldReceiveAResponseWithStatusCode(int statusCode) {
        assertThat(response.statusCode(), equalTo(statusCode));
    }

    @Then("the response body should contain {string}")
    public void theResponseBodyShouldContain(String expectedMessage) {
        assertThat(response.getBody().asString(), containsString(expectedMessage));
    }

    private Response authenticate(String username, String password) {
        var request = new SignInRequest(username, password);

        return response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .post(Routes.TOKEN);
    }

    private HttpHeaders getHttpHeaders() {
        String authToken = response.jsonPath().getString("accessToken");

        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + authToken);
        return httpHeaders;
    }
}
