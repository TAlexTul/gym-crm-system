package com.epam.gymcrmsystemapi.service.trainee;

import com.epam.gymcrmsystemapi.exceptions.TraineeExceptions;
import com.epam.gymcrmsystemapi.exceptions.TrainerExceptions;
import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeChangeTrainersSetRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeMergeRequest;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeSaveRequest;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeRegistrationResponse;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.user.KnownAuthority;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.TraineeRepository;
import com.epam.gymcrmsystemapi.repository.TrainerRepository;
import com.epam.gymcrmsystemapi.repository.TrainingRepository;
import com.epam.gymcrmsystemapi.service.user.UserOperations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class TraineeService implements TraineeOperations {

    private final UserOperations userOperations;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;

    public TraineeService(UserOperations userOperations,
                          TraineeRepository traineeRepository,
                          TrainerRepository trainerRepository,
                          TrainingRepository trainingRepository) {
        this.userOperations = userOperations;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
    }

    @Override
    public TraineeRegistrationResponse create(TraineeSaveRequest request) {
        return TraineeRegistrationResponse.fromTrainee(save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TraineeResponse> list(Pageable pageable) {
        return traineeRepository.findAll(pageable)
                .map(TraineeResponse::fromTraineeWithBasicAttribute);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TraineeResponse> findById(long id) {
        return traineeRepository.findById(id)
                .map(TraineeResponse::fromTrainee);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TraineeResponse> findByUsername(String username) {
        return traineeRepository.findByUsername(username)
                .map(TraineeResponse::fromTrainee);
    }

    @Override
    public TraineeResponse mergeById(long id, TraineeMergeRequest request) {
        Trainee trainee = getTrainee(id);
        return TraineeResponse.fromTrainee(merge(trainee, request));
    }

    @Override
    public TraineeResponse mergeByUsername(String username, TraineeMergeRequest request) {
        Trainee trainee = getTrainee(username);
        return TraineeResponse.fromTrainee(merge(trainee, request));
    }

    @Override
    public TraineeResponse changeStatusById(long id, UserStatus status) {
        userOperations.changeStatusById(id, status);
        return TraineeResponse.fromTrainee(getTrainee(id));
    }

    @Override
    public TraineeResponse changeStatusByUsername(String username, UserStatus status) {
        userOperations.changeStatusByUsername(username, status);
        return TraineeResponse.fromTrainee(getTrainee(username));
    }

    @Override
    public List<TrainerResponse> changeTraineeSetOfTrainers(TraineeChangeTrainersSetRequest request) {
        Trainee trainee = getTrainee(request.traineeUsername());
        Set<Trainer> trainers = trainee.getTrainers();
        trainers.clear();
        List<String> trainersUsernameList = request.trainerUsernames();
        for (String username : trainersUsernameList) {
            Trainer trainer = trainerRepository.findByUsername(username)
                    .orElseThrow(() -> TrainerExceptions.trainerNotFound(username));
            trainers.add(trainer);
        }

        return trainers.stream()
                .map(TrainerResponse::fromTrainer)
                .toList();
    }

    @Override
    public void deleteById(long id) {
        Trainee trainee = getTrainee(id);
        processTraineeTrainings(trainee);
        traineeRepository.deleteById(id);
    }

    @Override
    public void deleteByUsername(String username) {
        Trainee trainee = getTrainee(username);
        processTraineeTrainings(trainee);
        traineeRepository.deleteByUsername(username);
    }

    private Trainee save(TraineeSaveRequest request) {
        User user = userOperations.save(request.firstName(), request.lastName(), KnownAuthority.ROLE_TRAINEE);

        var trainee = new Trainee();
        trainee.setDateOfBirth(request.dateOfBirth());
        trainee.setAddress(request.address());
        trainee.setUser(user);
        return traineeRepository.save(trainee);
    }

    private Trainee getTrainee(long id) {
        return traineeRepository.findById(id)
                .orElseThrow(() -> TraineeExceptions.traineeNotFound(id));
    }

    private Trainee getTrainee(String username) {
        return traineeRepository.findByUsername(username)
                .orElseThrow(() -> TraineeExceptions.traineeNotFound(username));
    }

    private Trainee merge(Trainee trainee, TraineeMergeRequest request) {
        String firstName = request.firstName();
        if (firstName != null && !firstName.equals(trainee.getUser().getFirstName())) {
            trainee.getUser().setFirstName(firstName);
        }
        String lastName = request.lastName();
        if (lastName != null && !lastName.equals(trainee.getUser().getLastName())) {
            trainee.getUser().setLastName(lastName);
        }
        OffsetDateTime dateOfBirth = request.dateOfBirth();
        if (dateOfBirth != null && !dateOfBirth.equals(trainee.getDateOfBirth())) {
            trainee.setDateOfBirth(dateOfBirth);
        }
        String address = request.address();
        if (address != null && !address.equals(trainee.getAddress())) {
            trainee.setAddress(address);
        }
        UserStatus status = request.status();
        if (status != null && trainee.getUser().getStatus() != status) {
            trainee.getUser().setStatus(status);
        }
        return trainee;
    }

    private void processTraineeTrainings(Trainee trainee) {
        List<Training> trainings = trainingRepository.findAllByTrainees(trainee);
        for (Training training : trainings) {
            Set<Trainee> trainees = training.getTrainees();
            Set<Trainer> trainers = training.getTrainers();
            trainees.remove(trainee);
            if (trainees.size() == 0 && trainers.size() == 0) {
                trainingRepository.delete(training);
            }
        }
    }
}
