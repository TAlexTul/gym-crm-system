package com.epam.gymcrmsystemapi.cucumber.component;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.auth.request.SignInRequest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrainingTypeApiSteps {

    private Response response;

    @Given("a running application with an authorized default admin for training types endpoint")
    public void theAppIsRunningWithDefaultAdminCredentials() {
        response = authenticate("defaultadmin", "qwertyuiop");
    }

    @Given("a running application with an unauthorized default admin for training types endpoint")
    public void theAppIsRunningWithoutDefaultAdminCredentials() {
        response = authenticate("wrongusername", "wrongpassword");
    }

    @When("I retrieve all training types")
    public void iRetrieveAllTrainingTypes() {
        HttpHeaders httpHeaders = getHttpHeaders();

        response = RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(httpHeaders)
                .get(Routes.TRAINING_TYPES);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        assertThat(response.statusCode(), equalTo(statusCode));
    }

    @And("the response should contain the following training types:")
    public void theResponseShouldContainTheFollowingTrainingTypes(io.cucumber.datatable.DataTable dataTable) {
        List<?> res = response.as(List.class);

        List<List<String>> expectedData = dataTable.asLists(String.class);
        assertEquals(expectedData.size() - 1, res.size()); // Exclude header row
        for (int i = 1; i < expectedData.size(); i++) { // Skip header
            LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) res.get(i - 1);
            assertEquals(expectedData.get(i).get(0), String.valueOf(map.get("id")));
            assertEquals(expectedData.get(i).get(1), map.get("type"));
        }
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
