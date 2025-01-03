package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeChangeTrainersSetRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeMergeRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeSaveRequest;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeRegistrationResponse;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponseForTrainerResponse;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import com.epam.gymcrmsystemapi.model.trainer.specialization.response.SpecializationResponse;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.service.trainee.TraineeOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TraineeControllerTest {

    private MockMvc mvc;

    private TraineeOperations traineeOperations;

    @BeforeEach
    void setUp() {
        traineeOperations = mock(TraineeOperations.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new TraineeController(traineeOperations))
                .build();
    }

    @Test
    void testRegister() throws Exception {
        var id = 1L;
        var request = new TraineeSaveRequest("John", "Doe",
                OffsetDateTime.parse("2024-10-19T14:59:22.345Z"), "123 Main St");
        var response = new TraineeRegistrationResponse(id, "John.Doe", "aB9dE4fGhJ");

        when(traineeOperations.create(request)).thenReturn(response);

        var expectedJson = """
                {
                  "id": %s,
                  "username": "John.Doe",
                  "password": "aB9dE4fGhJ"
                }
                """.formatted(id);

        mvc.perform(post(Routes.TRAINEES)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                    {
                                       "firstName": "John",
                                       "lastName": "Doe",
                                       "dateOfBirth": "2024-10-19T14:59:22.345Z",
                                       "address": "123 Main St"
                                     }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Location",
                        containsString(String.format("%s/%d", Routes.TRAINEES, response.id()))))
                .andExpect(content().json(expectedJson));

        verify(traineeOperations, only()).create(request);
    }

    @Test
    void testGetById() throws Exception {
        var traineeId = 1L;
        var response = new TraineeResponse(
                "John", "Doe", UserStatus.ACTIVE,
                OffsetDateTime.parse("2024-10-19T14:59:22.345Z"), "123 Main St", new ArrayList<>());

        when(traineeOperations.findById(traineeId)).thenReturn(Optional.of(response));

        String expectedJson = getExpectedJson();

        mvc.perform(get(Routes.TRAINEES + "/" + traineeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(expectedJson));

        verify(traineeOperations, only()).findById(traineeId);
    }

    @Test
    void testMergeById() throws Exception {
        var traineeId = 1L;
        var request = new TraineeMergeRequest("John.Doe", "Sara", "Lesly",
                OffsetDateTime.parse("2024-10-19T14:59:22.345Z"), "123 Main St", UserStatus.ACTIVE);
        var response = new TraineeResponse(
                "Sara", "Lesly", UserStatus.ACTIVE,
                OffsetDateTime.parse("2024-10-19T14:59:22.345Z"), "123 Main St", new ArrayList<>());

        when(traineeOperations.mergeById(traineeId, request)).thenReturn(response);

        var expectedJson = """
                {
                  "firstName": "Sara",
                  "lastName": "Lesly",
                  "status": "ACTIVE",
                  "dateOfBirth": "2024-10-19T14:59:22.345Z",
                  "address": "123 Main St",
                  "trainers": []
                }
                """;

        mvc.perform(patch(Routes.TRAINEES + "/" + traineeId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                     {
                                       "username": "John.Doe",
                                       "firstName": "Sara",
                                       "lastName": "Lesly",
                                       "dateOfBirth": "2024-10-19T14:59:22.345Z",
                                       "address": "123 Main St",
                                       "status": "ACTIVE"
                                     }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(expectedJson));

        verify(traineeOperations, only()).mergeById(traineeId, request);
    }

    @Test
    void testChangeTraineeStatusById() throws Exception {
        var traineeId = 1L;
        var status = UserStatus.ACTIVE;
        var response = new TraineeResponse(
                "John", "Doe", status,
                OffsetDateTime.parse("2024-10-19T14:59:22.345Z"), "123 Main St", new ArrayList<>());

        when(traineeOperations.changeStatusById(traineeId, status)).thenReturn(response);

        String expectedJson = getExpectedJson();

        mvc.perform(patch(Routes.TRAINEES + "/" + traineeId + "/status")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                    {
                                      "status": "ACTIVE"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(expectedJson));

        verify(traineeOperations, only()).changeStatusById(traineeId, status);
    }

    @Test
    void testChangeTraineeStatusByUsername() throws Exception {
        var username = "John.Doe";
        var status = UserStatus.ACTIVE;
        var response = new TraineeResponse(
                "John", "Doe", status,
                OffsetDateTime.parse("2024-10-19T14:59:22.345Z"), "123 Main St", new ArrayList<>());

        when(traineeOperations.changeStatusByUsername(username, status)).thenReturn(response);

        String expectedJson = getExpectedJson();

        mvc.perform(patch(Routes.TRAINEES + "/status?username=" + username)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                    {
                                      "status": "ACTIVE"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(expectedJson));

        verify(traineeOperations, only()).changeStatusByUsername(username, status);
    }

    @Test
    void testChangeTrainersSet() throws Exception {
        var request = new TraineeChangeTrainersSetRequest("John.Doe", List.of("Jane.Jenkins"));
        var response = List.of(new TrainerResponse(
                "Jane.Jenkins", "Jane", "Jenkins", UserStatus.ACTIVE,
                SpecializationResponse.fromSpecialization(
                        new Specialization(SpecializationType.PERSONAL_TRAINER, SpecializationType.PERSONAL_TRAINER)),
                List.of(new TraineeResponseForTrainerResponse("John.Doe", "John", "Doe"))));

        String expectedJson = """
                [
                    {
                      "username": "Jane.Jenkins",
                      "firstName": "Jane",
                      "lastName": "Jenkins",
                      "status": "ACTIVE",
                      "specialization": {
                        "id": 0,
                        "specializationType": "PERSONAL_TRAINER"
                      },
                      "trainees": [
                        {
                          "username": "John.Doe",
                          "firstName": "John",
                          "lastName": "Doe"
                        }
                      ]
                    }
                ]
                """;
        when(traineeOperations.changeTraineeSetOfTrainers(request)).thenReturn(response);


        mvc.perform(patch(Routes.TRAINEES + "/change")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                     {
                                       "traineeUsername": "John.Doe",
                                       "trainerUsernames": [
                                         "Jane.Jenkins"
                                       ]
                                     }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(expectedJson));

        verify(traineeOperations, only()).changeTraineeSetOfTrainers(request);
    }

    @Test
    void testDeleteById() throws Exception {
        var id = 1L;

        doNothing().when(traineeOperations).deleteById(id);

        mvc.perform(delete(Routes.TRAINEES + "/" + id))
                .andExpect(status().isNoContent());

        verify(traineeOperations, only()).deleteById(id);
    }

    private String getExpectedJson() {
        return """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "status": "ACTIVE",
                  "dateOfBirth": "2024-10-19T14:59:22.345Z",
                  "address": "123 Main St",
                  "trainers": []
                }
                """;
    }
}
