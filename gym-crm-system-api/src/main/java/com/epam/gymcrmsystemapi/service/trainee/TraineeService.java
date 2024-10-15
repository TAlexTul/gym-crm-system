package com.epam.gymcrmsystemapi.service.trainee;

import com.epam.gymcrmsystemapi.exceptions.TraineeExceptions;
import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeSaveMergeRequest;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.user.OverridePasswordRequest;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.TraineeRepository;
import com.epam.gymcrmsystemapi.repository.TrainingRepository;
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

    private final TraineeRepository traineeRepository;
    private final TrainingRepository trainingRepository;
    private final PasswordEncoder passwordEncoder;

    public TraineeService(TraineeRepository traineeRepository,
                          TrainingRepository trainingRepository,
                          PasswordEncoder passwordEncoder) {
        this.traineeRepository = traineeRepository;
        this.trainingRepository = trainingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TraineeResponse create(TraineeSaveMergeRequest request) {
        return TraineeResponse.fromTrainee(save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TraineeResponse> list(Pageable pageable) {
        return traineeRepository.findAll(pageable)
                .map(TraineeResponse::fromTrainee);
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
    public TraineeResponse mergeById(long id, TraineeSaveMergeRequest request) {
        Trainee trainee = getTrainee(id);
        return TraineeResponse.fromTrainee(merge(trainee, request));
    }

    @Override
    public TraineeResponse mergeByUsername(String username, TraineeSaveMergeRequest request) {
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
    public TraineeResponse changePasswordById(long id, OverridePasswordRequest request) {
        Trainee trainee = getTrainee(id);
        trainee.getUser().setPassword(passwordEncoder.encode(request.password()));
        return TraineeResponse.fromTrainee(trainee);
    }

    @Override
    public TraineeResponse changePasswordByUsername(String username, OverridePasswordRequest request) {
        Trainee trainee = getTrainee(username);
        trainee.getUser().setPassword(passwordEncoder.encode(request.password()));
        return TraineeResponse.fromTrainee(trainee);
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

    private Trainee save(TraineeSaveMergeRequest request) {
        var trainee = new Trainee();
        trainee.setDateOfBirth(request.dateOfBirth());
        trainee.setAddress(request.address());
        trainee.setUser(createUser(request));
        return traineeRepository.save(trainee);
    }

    private User createUser(TraineeSaveMergeRequest request) {
        var user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(calculateUserName(request));
        user.setPassword(passwordEncoder.encode(generateRandomPassword()));
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private String calculateUserName(TraineeSaveMergeRequest request) {
        boolean isExist = traineeRepository.existsByFirstNameAndLastName(request.firstName(), request.lastName());
        if (isExist) {
            Trainee trainee = traineeRepository.findByFirstNameAndLastName(request.firstName(), request.lastName())
                    .orElseThrow(() -> TraineeExceptions.traineeNotFound(request.firstName(), request.lastName()));
            return String.join(".",
                    request.firstName().trim(),
                    request.lastName().trim(),
                    trainee.getUser().getId().toString());
        } else {
            return String.join(".",
                    request.firstName().trim(),
                    request.lastName().trim());
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

    private Trainee getTrainee(String userName) {
        return traineeRepository.findByUsername(userName)
                .orElseThrow(() -> TraineeExceptions.traineeNotFound(userName));
    }

    private Trainee merge(Trainee trainee, TraineeSaveMergeRequest request) {
        boolean isNameUpdated = false;

        String firstName = request.firstName();
        if (firstName != null && !firstName.equals(trainee.getUser().getFirstName())) {
            trainee.getUser().setFirstName(firstName);
            isNameUpdated = true;
        }
        String lastName = request.lastName();
        if (lastName != null && !lastName.equals(trainee.getUser().getLastName())) {
            trainee.getUser().setLastName(lastName);
            isNameUpdated = true;
        }
        if (isNameUpdated) {
            String oldUserName = trainee.getUser().getUsername();
            String numericPart = oldUserName.replaceAll("\\D.*", "");
            trainee.getUser().setUsername(numericPart + calculateUserName(request));
        }
        OffsetDateTime dateOfBirth = request.dateOfBirth();
        if (dateOfBirth != null && !dateOfBirth.equals(trainee.getDateOfBirth())) {
            trainee.setDateOfBirth(dateOfBirth);
        }
        String address = request.address();
        if (address != null && !address.equals(trainee.getAddress())) {
            trainee.setAddress(address);
        }

        return trainee;
    }

    private void processTraineeTrainings(Trainee trainee) {
        List<Training> trainings = trainingRepository.findAllByTrainees(trainee);
        for (Training training : trainings) {
            Set<Trainee> trainees = training.getTrainees();
            if (trainees.size() == 1) {
                trainingRepository.delete(training);
            } else {
                trainees.remove(trainee);
            }
        }
    }
}
