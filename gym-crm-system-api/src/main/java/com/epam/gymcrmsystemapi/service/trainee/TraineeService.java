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
import com.epam.gymcrmsystemapi.model.user.OverrideLoginRequest;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.TraineeRepository;
import com.epam.gymcrmsystemapi.repository.TrainerRepository;
import com.epam.gymcrmsystemapi.repository.TrainingRepository;
import com.epam.gymcrmsystemapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class TraineeService implements TraineeOperations {

    @Value("${password.length}")
    private int passwordLength;
    @Value("${password.characters}")
    private String passwordCharacters;

    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final PasswordEncoder passwordEncoder;

    public TraineeService(UserRepository userRepository,
                          TraineeRepository traineeRepository,
                          TrainerRepository trainerRepository,
                          TrainingRepository trainingRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
        this.passwordEncoder = passwordEncoder;
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
        Trainee trainee = getTrainee(id);
        if (trainee.getUser().getStatus() != status) {
            trainee.getUser().setStatus(status);
        }
        return TraineeResponse.fromTrainee(trainee);
    }

    @Override
    public TraineeResponse changeStatusByUsername(String username, UserStatus status) {
        Trainee trainee = getTrainee(username);
        if (trainee.getUser().getStatus() != status) {
            trainee.getUser().setStatus(status);
        }
        return TraineeResponse.fromTrainee(trainee);
    }

    @Override
    public TraineeResponse changeLoginDataById(long id, OverrideLoginRequest request) {
        Trainee trainee = getTrainee(id);
        trainee.getUser().setUsername(request.username());
        changePassword(trainee, request.oldPassword(), request.newPassword());
        return TraineeResponse.fromTrainee(trainee);
    }

    @Override
    public TraineeResponse changeLoginDataByUsername(String username, OverrideLoginRequest request) {
        Trainee trainee = getTrainee(username);
        trainee.getUser().setUsername(request.username());
        changePassword(trainee, request.oldPassword(), request.newPassword());
        return TraineeResponse.fromTrainee(trainee);
    }

    @Override
    public List<TrainerResponse> changeTraineeSetOfTrainers(TraineeChangeTrainersSetRequest request) {
        Trainee trainee = getTrainee(request.traineeUsername());
        Set<Trainer> trainers = trainee.getTrainers();
        List<String> trainersUsernameList = request.trainerUsernames();
        for (String username : trainersUsernameList) {
            Trainer trainer = trainerRepository.findByUsername(username)
                    .orElseThrow(() -> TrainerExceptions.trainerNotFound(username));
            trainers.add(trainer);
        }
        trainee.setTrainers(trainers);
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
        var trainee = new Trainee();
        trainee.setDateOfBirth(request.dateOfBirth());
        trainee.setAddress(request.address());
        trainee.setUser(createUser(request));
        return traineeRepository.save(trainee);
    }

    private User createUser(TraineeSaveRequest request) {
        String username = calculateUsername(request.firstName(), request.lastName());
        validateRegisteredAsTrainerOrTrainee(username);
        String password = generateRandomPassword();

        var user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        return new User(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                password,
                user.getStatus()
        );
    }

    private void validateRegisteredAsTrainerOrTrainee(String username) {
        if (trainerRepository.existsByUsername(username)) throw TraineeExceptions.usernameAlreadyRegistered(username);
    }

    private String calculateUsername(String firstName, String lastName) {
        boolean isExists = traineeRepository.existsByFirstNameAndLastName(firstName, lastName);
        if (isExists) {
            Trainee trainee = traineeRepository.findByFirstNameAndLastName(firstName, lastName)
                    .orElseThrow(() -> TraineeExceptions.traineeNotFound(firstName, lastName));
            return String.join(".",
                    firstName.trim(),
                    lastName.trim(),
                    trainee.getUser().getId().toString());
        } else {
            return String.join(".",
                    firstName.trim(),
                    lastName.trim());
        }
    }

    private String generateRandomPassword() {
        var random = new SecureRandom();
        var password = new StringBuilder();

        for (int i = 0; i < passwordLength; i++) {
            int randomIndex = random.nextInt(passwordCharacters.length());
            char randomChar = passwordCharacters.charAt(randomIndex);
            password.append(randomChar);
        }

        return password.toString();
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
        String oldUsername = trainee.getUser().getUsername();
        String newUsername = request.username();
        if (newUsername != null && !newUsername.equals(oldUsername) && traineeRepository.existsByUsername(newUsername)) {
            throw TraineeExceptions.duplicateUsername(newUsername);
        }

        String firstName = request.firstName();
        if (firstName != null && !firstName.equals(trainee.getUser().getFirstName())) {
            trainee.getUser().setFirstName(firstName);
        }
        String lastName = request.lastName();
        if (lastName != null && !lastName.equals(trainee.getUser().getLastName())) {
            trainee.getUser().setLastName(lastName);
        }
        if (newUsername != null && !newUsername.equals(oldUsername)) {
            trainee.getUser().setUsername(newUsername);
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

    private void changePassword(Trainee trainee, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, trainee.getUser().getPassword())) {
            throw TraineeExceptions.wrongPassword();
        }
        trainee.getUser().setPassword(passwordEncoder.encode(newPassword));
    }
}
