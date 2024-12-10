package com.epam.gymcrmsystemapi.service.workload;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import com.epam.gymcrmsystemapi.model.training.request.ProvidedTrainingDeleteRequest;
import com.epam.gymcrmsystemapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;
import com.epam.gymcrmsystemapi.model.training.type.Type;
import com.epam.gymcrmsystemapi.model.training.type.response.TrainingTypeResponse;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.service.workload.activemq.MessageProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class TrainerWorkloadServiceTest {

    @Mock
    private MessageProducer messageProducer;

    @InjectMocks
    private TrainerWorkloadService trainerWorkloadService;

    String trainingName;
    OffsetDateTime trainingDate;
    long trainingDuration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        trainingName = "Training1";
        trainingDate = OffsetDateTime.parse("2007-12-03T09:15:30Z");
        trainingDuration = 30000L;
    }

    @Test
    void testInvoke_TrainingSaveRequest_TrainingResponse() {
        var trainee = getTrainee();
        var trainer = getTrainer();
        var traineeUsername = trainee.getUser().getUsername();
        var trainerUsername = trainer.getUser().getUsername();
        var trainingType = Type.FUNCTIONAL_TRAINING;

        var request = new TrainingSaveRequest(
                traineeUsername, trainerUsername, trainingName, trainingType, trainingDate, trainingDuration);

        var response = new TrainingResponse(
                1L,
                getTraineeResponses(),
                getTrainerResponses(),
                trainingName,
                List.of(new TrainingTypeResponse(trainingType.ordinal(), trainingType)),
                trainingDate,
                trainingDuration
        );

        var providedRequest = new ProvidedTrainingSaveRequest(
                trainerUsername,
                "Jane",
                "Jenkins",
                UserStatus.ACTIVE,
                trainingDate,
                trainingDuration
        );

        doNothing().when(messageProducer).processAndSend(providedRequest);

        trainerWorkloadService.invoke(request, response);

        verify(messageProducer, times(1)).processAndSend(providedRequest);
    }

    @Test
    void testInvoke_TrainingResponse() {
        var response = new TrainingResponse(
                1L,
                getTraineeResponses(),
                getTrainerResponses(),
                trainingName,
                List.of(),
                trainingDate,
                trainingDuration
        );

        List<String> trainerUsernames = response.trainers().stream()
                .map(TrainerResponse::username)
                .toList();

        var request = new ProvidedTrainingDeleteRequest(
                String.join(",", trainerUsernames),
                response.trainingDate(),
                response.trainingDuration()
        );

        doNothing().when(messageProducer).processAndSend(request);

        trainerWorkloadService.invoke(response);

        verify(messageProducer, times(1)).processAndSend(request);
    }


    private Set<TraineeResponse> getTraineeResponses() {
        return Stream.of(getTrainee())
                .map(TraineeResponse::fromTrainee)
                .collect(Collectors.toSet());
    }

    private Set<TrainerResponse> getTrainerResponses() {
        return Stream.of(getTrainer())
                .map(TrainerResponse::fromTrainer)
                .collect(Collectors.toSet());
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
