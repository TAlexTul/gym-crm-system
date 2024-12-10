package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerMergeRequest;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerSaveRequest;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerRegistrationResponse;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import com.epam.gymcrmsystemapi.model.trainer.specialization.response.SpecializationResponse;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.service.trainer.TrainerOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TrainerControllerTest {

    private MockMvc mvc;

    private TrainerOperations trainerOperations;

    @BeforeEach
    void setUp() {
        trainerOperations = mock(TrainerOperations.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new TrainerController(trainerOperations))
                .build();
    }

    @Test
    void testRegister() throws Exception {
        var id = 1L;
        var request = new TrainerSaveRequest("Jane", "Jenkins", SpecializationType.PERSONAL_TRAINER);
        var response = new TrainerRegistrationResponse(id, "Jane.Jenkins", "aB9dE4fGhJ");

        when(trainerOperations.create(request)).thenReturn(response);

        var expectedJson = """
                {
                  "id": %s,
                  "userName": "Jane.Jenkins",
                  "password": "aB9dE4fGhJ"
                }
                """.formatted(id);

        mvc.perform(post(Routes.TRAINERS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                       "firstName": "Jane",
                                       "lastName": "Jenkins",
                                       "specializationType": "PERSONAL_TRAINER"
                                     }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Location",
                        containsString(String.format("%s/%d", Routes.TRAINERS, response.id()))))
                .andExpect(content().json(expectedJson));

        verify(trainerOperations, only()).create(request);
    }

    @Test
    void testGetById() throws Exception {
        var trainerId = 1L;
        var response = new TrainerResponse(
                "Jane,Jenkins", "Jane", "Jenkins", UserStatus.ACTIVE,
                SpecializationResponse.fromSpecialization(
                        new Specialization(SpecializationType.PERSONAL_TRAINER, SpecializationType.PERSONAL_TRAINER)),
                new ArrayList<>());

        when(trainerOperations.findById(trainerId)).thenReturn(Optional.of(response));

        String expectedJson = getExpectedJson();

        mvc.perform(get(Routes.TRAINERS + "/" + trainerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(trainerOperations, only()).findById(trainerId);
    }

    @Test
    void testMergeById() throws Exception {
        var trainerId = 1L;
        var request = new TrainerMergeRequest("Sara.Lesly", "Jane", "Jenkins", UserStatus.ACTIVE,
                new Specialization(SpecializationType.PERSONAL_TRAINER, SpecializationType.PERSONAL_TRAINER));
        var response = new TrainerResponse(
                "Jane,Jenkins" , "Jane", "Jenkins", UserStatus.ACTIVE,
                SpecializationResponse.fromSpecialization(
                        new Specialization(SpecializationType.PERSONAL_TRAINER, SpecializationType.PERSONAL_TRAINER)),
                new ArrayList<>());

        when(trainerOperations.mergeById(trainerId, request)).thenReturn(response);

        var expectedJson = getExpectedJson();

        mvc.perform(patch(Routes.TRAINERS + "/" + trainerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                     {
                                       "username": "Sara.Lesly",
                                       "firstName": "Jane",
                                       "lastName": "Jenkins",
                                       "status": "ACTIVE",
                                       "specialization": {
                                         "id": "PERSONAL_TRAINER",
                                         "specialization": "PERSONAL_TRAINER"
                                       }
                                     }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));

        verify(trainerOperations, only()).mergeById(trainerId, request);
    }

    @Test
    void testChangeTrainerStatusById() throws Exception {
        var trainerId = 1L;
        var status = UserStatus.ACTIVE;
        var response = new TrainerResponse("Jane,Jenkins", "Jane", "Jenkins", status,
                SpecializationResponse.fromSpecialization(
                        new Specialization(SpecializationType.PERSONAL_TRAINER, SpecializationType.PERSONAL_TRAINER)),
                new ArrayList<>());

        when(trainerOperations.changeStatusById(trainerId, status)).thenReturn(response);

        var expectedJson = getExpectedJson();
        mvc.perform(patch(Routes.TRAINERS + "/" + trainerId + "/status")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                    {
                                      "status": "ACTIVE"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(expectedJson));

        verify(trainerOperations, only()).changeStatusById(trainerId, status);
    }

    @Test
    void testChangeTrainerStatusByUsername() throws Exception {
        var username = "Sara.Lesly";
        var status = UserStatus.ACTIVE;
        var response = new TrainerResponse("Jane,Jenkins", "Jane", "Jenkins", status,
                SpecializationResponse.fromSpecialization(
                        new Specialization(SpecializationType.PERSONAL_TRAINER, SpecializationType.PERSONAL_TRAINER)),
                new ArrayList<>());

        when(trainerOperations.changeStatusByUsername(username, status)).thenReturn(response);

        var expectedJson = getExpectedJson();
        mvc.perform(patch(Routes.TRAINERS + "/status?username=" + username)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                    {
                                      "status": "ACTIVE"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(expectedJson));

        verify(trainerOperations, only()).changeStatusByUsername(username, status);
    }

    @Test
    void testDeleteById() throws Exception {
        var id = 1L;

        doNothing().when(trainerOperations).deleteById(id);

        mvc.perform(delete(Routes.TRAINERS + "/" + id))
                .andExpect(status().isNoContent());

        verify(trainerOperations, only()).deleteById(id);
    }

    private String getExpectedJson() {
        return """
                {
                  "username": "Jane,Jenkins",
                  "firstName": "Jane",
                  "lastName": "Jenkins",
                  "status": "ACTIVE",
                  "specialization": {
                    "id": 0,
                    "specializationType": "PERSONAL_TRAINER"
                  },
                  "trainees": []
                }
                """;
    }
}
