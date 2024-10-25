package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeMergeRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeSaveRequest;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeRegistrationResponse;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.service.trainee.TraineeOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.ArrayList;
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
                  "userName": "John.Doe",
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

        var expectedJson = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "status": "ACTIVE",
                  "dateOfBirth": "2024-10-19T14:59:22.345Z",
                  "address": "123 Main St",
                  "trainers": []
                }
                """;

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
                                       "username" : "John.Doe",
                                       "firstName": "Sara",
                                       "lastName": "Lesly",
                                       "dateOfBirth": "2024-10-19T14:59:22.345Z",
                                       "address": "123 Main St",
                                       "status" : "ACTIVE"
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
                "Sara", "Lesly", status,
                OffsetDateTime.parse("2024-10-19T14:59:22.345Z"), "123 Main St", new ArrayList<>());

        when(traineeOperations.changeStatusById(traineeId, status)).thenReturn(response);

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
        var username = "Sara.Lesly";
        var status = UserStatus.ACTIVE;
        var response = new TraineeResponse(
                "Sara", "Lesly", status,
                OffsetDateTime.parse("2024-10-19T14:59:22.345Z"), "123 Main St", new ArrayList<>());

        when(traineeOperations.changeStatusByUsername(username, status)).thenReturn(response);

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
    void testDeleteById() throws Exception {
        var id = 1L;

        doNothing().when(traineeOperations).deleteById(id);

        mvc.perform(delete(Routes.TRAINEES + "/" + id))
                .andExpect(status().isNoContent());

        verify(traineeOperations, only()).deleteById(id);
    }
}
