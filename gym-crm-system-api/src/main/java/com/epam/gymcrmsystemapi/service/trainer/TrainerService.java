package com.epam.gymcrmsystemapi.service.trainer;

import com.epam.gymcrmsystemapi.exceptions.SpecializationExceptions;
import com.epam.gymcrmsystemapi.exceptions.TraineeExceptions;
import com.epam.gymcrmsystemapi.exceptions.TrainerExceptions;
import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerMergeRequest;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerSaveRequest;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerRegistrationResponse;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.user.OverrideLoginRequest;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.SpecializationRepository;
import com.epam.gymcrmsystemapi.repository.TraineeRepository;
import com.epam.gymcrmsystemapi.repository.TrainerRepository;
import com.epam.gymcrmsystemapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrainerService implements TrainerOperations {

    @Value("${password.length}")
    private int passwordLength;
    @Value("${password.characters}")
    private String passwordCharacters;

    private final SpecializationRepository specializationRepository;
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final PasswordEncoder passwordEncoder;

    public TrainerService(SpecializationRepository specializationRepository,
                          UserRepository userRepository,
                          TrainerRepository trainerRepository,
                          TraineeRepository traineeRepository,
                          PasswordEncoder passwordEncoder) {
        this.specializationRepository = specializationRepository;
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TrainerRegistrationResponse create(TrainerSaveRequest request) {
        return TrainerRegistrationResponse.fromTrainer(save(request));
    }

    @Override
    public Page<TrainerResponse> list(Pageable pageable) {
        return trainerRepository.findAll(pageable)
                .map(TrainerResponse::fromTrainerWithBasicAttributes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerResponse> listOfTrainersNotAssignedByTraineeUsername(String username) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> TraineeExceptions.traineeNotFound(username));

        return trainerRepository.findAllNotAssignedToTrainee(trainee).stream()
                .map(TrainerResponse::fromTrainerWithBasicAttributes)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrainerResponse> findById(long id) {
        return trainerRepository.findById(id)
                .map(TrainerResponse::fromTrainer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrainerResponse> findByUsername(String username) {
        return trainerRepository.findByUsername(username)
                .map(TrainerResponse::fromTrainer);
    }

    @Override
    public TrainerResponse mergeById(long id, TrainerMergeRequest request) {
        Trainer trainer = getTrainer(id);
        return TrainerResponse.fromTrainer(merge(trainer, request));
    }

    @Override
    public TrainerResponse mergeByUsername(String username, TrainerMergeRequest request) {
        Trainer trainer = getTrainer(username);
        return TrainerResponse.fromTrainer(merge(trainer, request));
    }

    @Override
    public TrainerResponse changeStatusById(long id, UserStatus status) {
        Trainer trainer = getTrainer(id);
        if (trainer.getUser().getStatus() != status) {
            trainer.getUser().setStatus(status);
        }
        return TrainerResponse.fromTrainer(trainer);
    }

    @Override
    public TrainerResponse changeStatusByUsername(String username, UserStatus status) {
        Trainer trainer = getTrainer(username);
        if (trainer.getUser().getStatus() != status) {
            trainer.getUser().setStatus(status);
        }
        return TrainerResponse.fromTrainer(trainer);
    }

    @Override
    public TrainerResponse changeLoginDataById(long id, OverrideLoginRequest request) {
        Trainer trainer = getTrainer(id);
        trainer.getUser().setUsername(request.username());
        changePassword(trainer, request.oldPassword(), request.newPassword());
        return TrainerResponse.fromTrainer(trainer);
    }

    @Override
    public TrainerResponse changeLoginDataByUsername(String username, OverrideLoginRequest request) {
        Trainer trainer = getTrainer(username);
        trainer.getUser().setUsername(request.username());
        changePassword(trainer, request.oldPassword(), request.newPassword());
        return TrainerResponse.fromTrainer(trainer);
    }

    @Override
    public void deleteById(long id) {
        trainerRepository.deleteById(id);
    }

    @Override
    public void deleteByUsername(String username) {
        trainerRepository.deleteByUsername(username);
    }

    private Trainer save(TrainerSaveRequest request) {
        Specialization specialization = specializationRepository.findById(request.specializationType())
                .orElseThrow(() -> SpecializationExceptions.specializationNotFound(request.specializationType()));
        var trainer = new Trainer();
        trainer.setSpecialization(specialization);
        trainer.setUser(createUser(request));
        return trainerRepository.save(trainer);
    }

    private User createUser(TrainerSaveRequest request) {
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
        if (traineeRepository.existsByUsername(username)) throw TrainerExceptions.usernameAlreadyRegistered(username);
    }

    private String calculateUsername(String firstName, String lastName) {
        boolean isExists = trainerRepository.existsByFirstNameAndLastName(firstName, lastName);
        if (isExists) {
            Trainer trainer = trainerRepository.findByFirstNameAndLastName(firstName, lastName)
                    .orElseThrow(() -> TrainerExceptions.trainerNotFound(firstName, lastName));
            return String.join(".",
                    firstName.trim(),
                    lastName.trim(),
                    trainer.getUser().getId().toString());
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

    private Trainer getTrainer(long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> TrainerExceptions.trainerNotFound(id));
    }

    private Trainer getTrainer(String userName) {
        return trainerRepository.findByUsername(userName)
                .orElseThrow(() -> TrainerExceptions.trainerNotFound(userName));
    }

    private Trainer merge(Trainer trainer, TrainerMergeRequest request) {
        String oldUsername = trainer.getUser().getUsername();
        String newUsername = request.username();
        if (newUsername != null && !newUsername.equals(oldUsername) && trainerRepository.existsByUsername(newUsername)) {
            throw TrainerExceptions.duplicateUsername(newUsername);
        }

        String firstName = request.firstName();
        if (firstName != null && !firstName.equals(trainer.getUser().getFirstName())) {
            trainer.getUser().setFirstName(firstName);
        }
        String lastName = request.lastName();
        if (lastName != null && !lastName.equals(trainer.getUser().getLastName())) {
            trainer.getUser().setLastName(lastName);
        }
        if (newUsername != null && !newUsername.equals(oldUsername)) {
            trainer.getUser().setUsername(newUsername);
        }
        Specialization specialization = request.specialization();
        if (specialization != null && !specialization.equals(trainer.getSpecialization())) {
            trainer.setSpecialization(specialization);
        }
        UserStatus status = request.status();
        if (status != null && trainer.getUser().getStatus() != status) {
            trainer.getUser().setStatus(status);
        }

        return trainer;
    }

    private void changePassword(Trainer trainer, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, trainer.getUser().getPassword())) {
            throw TrainerExceptions.wrongPassword();
        }
        trainer.getUser().setPassword(passwordEncoder.encode(newPassword));
    }
}
