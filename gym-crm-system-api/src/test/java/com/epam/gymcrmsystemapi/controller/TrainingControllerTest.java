package com.epam.gymcrmsystemapi.controller;

import com.epam.gymcrmsystemapi.Routes;
import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;
import com.epam.gymcrmsystemapi.model.training.type.TrainingType;
import com.epam.gymcrmsystemapi.model.training.type.Type;
import com.epam.gymcrmsystemapi.model.training.type.response.TrainingTypeResponse;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.service.training.TrainingOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TrainingControllerTest {

    private MockMvc mvc;

    private TrainingOperations trainingOperations;

    @BeforeEach
    void setUp() {
        trainingOperations = mock(TrainingOperations.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new TrainingController(trainingOperations))
                .build();
    }

    @Test
    void testRegister() throws Exception {
        var request = new TrainingSaveRequest(
                "John.Doe",
                "Jane.Jenkins",
                "Training 1",
                Type.STRENGTH_TRAINING,
                OffsetDateTime.parse("2024-10-19T14:23:09.31Z"),
                300000L);

        var response = new TrainingResponse(
                1L,
                getTraineeResponses(Set.of(getTrainee())),
                getTrainerResponses(Set.of(getTrainer())),
                "Training 1",
                getTrainingTypes(List.of(new TrainingType(Type.STRENGTH_TRAINING, Type.STRENGTH_TRAINING))),
                OffsetDateTime.parse("2024-10-19T14:23:09.31Z"),
                300000L);

        when(trainingOperations.create(request)).thenReturn(response);

        var expectedJson = getExpectedJson();

        mvc.perform(post(Routes.TRAININGS)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("""
                                    {
                                      "traineeUsername": "John.Doe",
                                      "trainerUsername": "Jane.Jenkins",
                                      "trainingName": "Training 1",
                                      "trainingType": "STRENGTH_TRAINING",
                                      "trainingDate": "2024-10-19T14:23:09.31Z",
                                      "trainingDuration": 300000
                                    }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string("Location",
                        containsString(String.format("%s/%d", Routes.TRAININGS, response.id()))))
                .andExpect(content().json(expectedJson));

        verify(trainingOperations, only()).create(request);
    }

    @Test
    void testGetById() throws Exception {
        var id = 1L;
        var response = new TrainingResponse(
                1L,
                getTraineeResponses(Set.of(getTrainee())),
                getTrainerResponses(Set.of(getTrainer())),
                "Training 1",
                getTrainingTypes(List.of(new TrainingType(Type.STRENGTH_TRAINING, Type.STRENGTH_TRAINING))),
                OffsetDateTime.parse("2024-10-19T14:23:09.31Z"),
                300000L);

        when(trainingOperations.findById(id)).thenReturn(Optional.of(response));

        var expectedJson = getExpectedJson();

        mvc.perform(get(Routes.TRAININGS + "/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(expectedJson));

        verify(trainingOperations, only()).findById(id);
    }

    private String getExpectedJson() {
        return """
                {
                  "id": 1,
                  "trainees": [
                    {
                      "firstName": "John",
                      "lastName": "Doe",
                      "status": "ACTIVE",
                      "dateOfBirth": "2007-12-03T09:15:30Z",
                      "address": "123 Main St",
                      "trainers": []
                    }
                  ],
                  "trainers": [
                    {
                      "firstName": "Jane",
                      "lastName": "Jenkins",
                      "status": "ACTIVE",
                      "specialization": {
                        "id": 0,
                        "specializationType": "PERSONAL_TRAINER"
                      },
                      "trainees": []
                    }
                  ],
                  "trainingName": "Training 1",
                  "trainingTypes": [
                    {
                      "id": 0,
                      "type": "STRENGTH_TRAINING"
                    }
                  ],
                  "trainingDate": "2024-10-19T14:23:09.31Z",
                  "trainingDuration": 300000
                }
                """;
    }

    private Set<TraineeResponse> getTraineeResponses(Set<Trainee> trainees) {
        return trainees.stream()
                .map(TraineeResponse::fromTrainee)
                .collect(Collectors.toSet());
    }

    private Set<TrainerResponse> getTrainerResponses(Set<Trainer> trainers) {
        return trainers.stream()
                .map(TrainerResponse::fromTrainer)
                .collect(Collectors.toSet());
    }

    private static List<TrainingTypeResponse> getTrainingTypes(List<TrainingType> trainingTypes) {
        return trainingTypes.stream()
                .map(TrainingTypeResponse::fromTrainingType)
                .toList();
    }

    private Trainee getTrainee() {
        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setDateOfBirth(OffsetDateTime.parse("2007-12-03T09:15:30Z"));
        trainee.setAddress("123 Main St");
        trainee.setUser(getUserForTrainee());
        return trainee;
    }

    private Trainer getTrainer() {
        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setSpecialization(
                new Specialization(SpecializationType.PERSONAL_TRAINER, SpecializationType.PERSONAL_TRAINER));
        trainer.setUser(getUserTrainer());
        return trainer;
    }

    private User getUserForTrainee() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword("aB9dE4fGhJ");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private User getUserTrainer() {
        var user = new User();
        user.setId(2L);
        user.setFirstName("Jane");
        user.setLastName("Jenkins");
        user.setUsername("Jane.Jenkins");
        user.setPassword("aB9dE4fGhJ");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
