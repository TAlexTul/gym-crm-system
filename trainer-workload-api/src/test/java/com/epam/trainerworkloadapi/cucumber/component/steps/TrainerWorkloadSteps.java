package com.epam.trainerworkloadapi.cucumber.component.steps;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.epam.trainerworkloadapi.Routes;
import com.epam.trainerworkloadapi.config.security.SecurityConstants;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsRequest;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsResponse;
import com.epam.trainerworkloadapi.model.summary.SummaryTrainingsDuration;
import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.trainerworkloadapi.model.user.KnownAuthority;
import com.epam.trainerworkloadapi.model.user.UserStatus;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrainerWorkloadSteps {

    @Value("${host}")
    private String host;
    @Value("${security.jwt.secret}")
    private String secret;

    @Autowired
    private MongoTemplate mongoTemplate;

    private Response response;
    private final String trainerUsername;
    private final String trainerFirstName;
    private final String trainerLastName;
    private final UserStatus trainerStatus;
    private final OffsetDateTime trainingDate;
    private final long trainingDuration;
    private final SummaryTrainingsDuration std;
    private final Duration jwtExpiration;
    private Algorithm algorithm;

    public TrainerWorkloadSteps() {
        this.trainerUsername = "Jane.Jenkins";
        this.trainerFirstName = "Jane";
        this.trainerLastName = "Jenkins";
        this.trainerStatus = UserStatus.ACTIVE;
        this.trainingDate = OffsetDateTime.parse("2024-12-03T10:15:30+01:00");
        this.trainingDuration = 30000L;
        var providedTraining = new ProvidedTraining(
                Year.from(trainingDate),
                Month.from(trainingDate),
                trainingDuration
        );
        this.std = new SummaryTrainingsDuration();
        std.setUsername(trainerUsername);
        std.setFirstName(trainerFirstName);
        std.setLastName(trainerLastName);
        std.setUserStatus(trainerStatus);
        std.setTrainings(List.of(providedTraining));
        std.setSummaryTrainingsDuration(trainingDuration);
        this.jwtExpiration = Duration.ofMinutes(10);
    }

    @PostConstruct
    public void initAlgorithm() {
        this.algorithm = Algorithm.HMAC512(secret.getBytes());
    }

    @Given("the trainer workload API is available")
    public void theTrainerWorkloadApiIsAvailable() {
        RestAssured.baseURI = host;
    }

    @When("I send a valid request with trainerUsername {string}, trainerFirstName {string}, " +
            "trainerLastName {string}, trainerStatus {string} and yearMonth {string}")
    public void iSendAValidRequest(String trainerUsername, String trainerFirstName, String trainerLastName,
                                   String trainerStatus, String yearMonth) {
        createSummaryTrainingDuration(trainerUsername, trainerFirstName, trainerLastName, trainerStatus);

        var request = new MonthlySummaryTrainingsRequest(
                trainerUsername,
                trainerFirstName,
                trainerLastName,
                UserStatus.valueOf(trainerStatus),
                YearMonth.parse(yearMonth)
        );

        HttpHeaders headers = getHttpHeaders();

        response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
                .body(request)
                .post(Routes.WORKLOAD);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + issueJWT());
        return headers;
    }

    private String issueJWT() {
        long issuedAt = System.currentTimeMillis();
        return JWT.create()
                .withSubject(trainerUsername)
                .withIssuedAt(new Date(issuedAt))
                .withExpiresAt(new Date(issuedAt + jwtExpiration.toMillis()))
                .withArrayClaim(
                        SecurityConstants.AUTHORITIES_CLAIM, new String[] {KnownAuthority.ROLE_ADMIN.getAuthority()})
                .sign(algorithm);
    }

    @When("I send a request with invalid data")
    public void iSendARequestWithInvalidData() {
        var request = new MonthlySummaryTrainingsRequest(
                null,
                trainerFirstName,
                trainerLastName,
                trainerStatus,
                YearMonth.parse("2024-12")
        );

        HttpHeaders headers = getHttpHeaders();

        response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
                .body(request)
                .post(Routes.WORKLOAD);
    }

    @When("I send a request with trainer data that does not exist in the database")
    public void iSendARequestWithTrainerDataWhatNotExistInDB() {
        var request = new MonthlySummaryTrainingsRequest(
                "John.Doe",
                "John",
                "Doe",
                UserStatus.ACTIVE,
                YearMonth.parse("2024-12")
        );

        HttpHeaders headers = getHttpHeaders();

        response = RestAssured
                .given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .headers(headers)
                .body(request)
                .post(Routes.WORKLOAD);
    }

    @Then("I should receive a response with status {string}")
    public void iShouldReceiveAResponseWithStatus(String status) {
        assertEquals(Integer.parseInt(status), response.statusCode());
    }

    @Then("the response should contain the summary")
    public void theResponseShouldContainTheSummary() {
        MonthlySummaryTrainingsResponse resp = response.as(MonthlySummaryTrainingsResponse.class);

        assertNotNull(resp);
        assertEquals(trainerUsername, resp.trainerUsername());
        assertEquals(trainerFirstName, resp.trainerFirstName());
        assertEquals(trainerLastName, resp.trainerLastName());
        assertEquals(trainerStatus, resp.trainerStatus());
        assertEquals(std.getTrainings().size(), resp.trainings().size());
        assertEquals(std.getTrainings().get(0).getYear(), Year.from(trainingDate));
        assertEquals(std.getTrainings().get(0).getMonth(), Month.from(trainingDate));
        assertEquals(std.getTrainings().get(0).getTrainingDuration(), trainingDuration);
    }

    @Then("the response should contain an error message")
    public void theResponseShouldContainAnErrorMessage() {
        response.then().body("error", notNullValue());
    }

    private void createSummaryTrainingDuration(String trainerUsername, String trainerFirstName,
                                               String trainerLastName, String trainerStatus) {
        var request = new ProvidedTrainingSaveRequest(
                trainerUsername,
                trainerFirstName,
                trainerLastName,
                UserStatus.valueOf(trainerStatus),
                trainingDate,
                trainingDuration
        );

        List<ProvidedTraining> providedTrainings = getProvidedTrainings(request);
        long summaryTrainingsDuration = getSummaryTrainingsDuration(providedTrainings);

        var std = SummaryTrainingsDuration.builder()
                .username(trainerUsername)
                .firstName(trainerFirstName)
                .lastName(trainerLastName)
                .userStatus(UserStatus.valueOf(trainerStatus))
                .trainings(providedTrainings)
                .summaryTrainingsDuration(summaryTrainingsDuration)
                .build();

        mongoTemplate.save(std);
    }

    private List<ProvidedTraining> getProvidedTrainings(ProvidedTrainingSaveRequest request) {
        return new ArrayList<>(List.of(
                new ProvidedTraining(
                        Year.from(request.trainingDate()),
                        Month.from(request.trainingDate()),
                        request.trainingDuration()
                )));
    }

    private long getSummaryTrainingsDuration(List<ProvidedTraining> providedTrainings) {
        return providedTrainings.stream()
                .mapToLong(ProvidedTraining::getTrainingDuration)
                .sum();
    }
}
