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
import com.epam.gymcrmsystemapi.service.user.UserOperations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrainerService implements TrainerOperations {

    private final UserOperations userOperations;
    private final SpecializationRepository specializationRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final PasswordEncoder passwordEncoder;

    public TrainerService(UserOperations userOperations,
                          SpecializationRepository specializationRepository,
                          TrainerRepository trainerRepository,
                          TraineeRepository traineeRepository,
                          PasswordEncoder passwordEncoder) {
        this.userOperations = userOperations;
        this.specializationRepository = specializationRepository;
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
        userOperations.changeStatusById(id, status);
        return TrainerResponse.fromTrainer(getTrainer(id));
    }

    @Override
    public TrainerResponse changeStatusByUsername(String username, UserStatus status) {
        userOperations.changeStatusByUsername(username, status);
        return TrainerResponse.fromTrainer(getTrainer(username));
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
        User user = userOperations.save(request.firstName(), request.lastName());

        Specialization specialization = specializationRepository.findById(request.specializationType())
                .orElseThrow(() -> SpecializationExceptions.specializationNotFound(request.specializationType()));
        var trainer = new Trainer();
        trainer.setSpecialization(specialization);
        trainer.setUser(user);
        return trainerRepository.save(trainer);
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
        String firstName = request.firstName();
        if (firstName != null && !firstName.equals(trainer.getUser().getFirstName())) {
            trainer.getUser().setFirstName(firstName);
        }
        String lastName = request.lastName();
        if (lastName != null && !lastName.equals(trainer.getUser().getLastName())) {
            trainer.getUser().setLastName(lastName);
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
