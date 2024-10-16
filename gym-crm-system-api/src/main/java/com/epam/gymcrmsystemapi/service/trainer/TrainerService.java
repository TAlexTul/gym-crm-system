package com.epam.gymcrmsystemapi.service.trainer;

import com.epam.gymcrmsystemapi.exceptions.TrainerExceptions;
import com.epam.gymcrmsystemapi.model.trainer.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerSaveMergeRequest;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.user.OverridePasswordRequest;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@Transactional
public class TrainerService implements TrainerOperations {

    @Value("${password.length}")
    private int passwordLength;
    @Value("${password.characters}")
    private String passwordCharacters;

    private final TrainerRepository trainerRepository;

    private final PasswordEncoder passwordEncoder;

    public TrainerService(TrainerRepository trainerRepository, PasswordEncoder passwordEncoder) {
        this.trainerRepository = trainerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TrainerResponse create(TrainerSaveMergeRequest request) {
        return TrainerResponse.fromTrainer(save(request));
    }

    @Override
    public Page<TrainerResponse> list(Pageable pageable) {
        return trainerRepository.findAll(pageable)
                .map(TrainerResponse::fromTrainer);
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
    public TrainerResponse mergeById(long id, TrainerSaveMergeRequest request) {
        Trainer trainer = getTrainer(id);
        return TrainerResponse.fromTrainer(merge(trainer, request));
    }

    @Override
    public TrainerResponse mergeByUsername(String username, TrainerSaveMergeRequest request) {
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
    public TrainerResponse changePasswordById(long id, OverridePasswordRequest request) {
        Trainer trainer = getTrainer(id);
        trainer.getUser().setPassword(passwordEncoder.encode(request.password()));
        return TrainerResponse.fromTrainer(trainer);
    }

    @Override
    public TrainerResponse changePasswordByUsername(String username, OverridePasswordRequest request) {
        Trainer trainer = getTrainer(username);
        trainer.getUser().setPassword(passwordEncoder.encode(request.password()));
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

    private Trainer save(TrainerSaveMergeRequest request) {
        var trainer = new Trainer();
        trainer.setSpecialization(request.specialization());
        trainer.setUser(createUser(request));
        return trainerRepository.save(trainer);
    }

    private User createUser(TrainerSaveMergeRequest request) {
        var user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(calculateUserName(request));
        user.setPassword(passwordEncoder.encode(generateRandomPassword()));
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private String calculateUserName(TrainerSaveMergeRequest request) {
        boolean isExist = trainerRepository.existsByFirstNameAndLastName(request.firstName(), request.lastName());
        if (isExist) {
            Trainer trainer = trainerRepository.findByFirstNameAndLastName(request.firstName(), request.lastName())
                    .orElseThrow(() -> TrainerExceptions.trainerNotFound(request.firstName(), request.lastName()));
            return String.join(".",
                    request.firstName().trim(),
                    request.lastName().trim(),
                    trainer.getUser().getId().toString());
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

    private Trainer getTrainer(long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> TrainerExceptions.trainerNotFound(id));
    }

    private Trainer getTrainer(String userName) {
        return trainerRepository.findByUsername(userName)
                .orElseThrow(() -> TrainerExceptions.trainerNotFound(userName));
    }

    private Trainer merge(Trainer trainer, TrainerSaveMergeRequest request) {
        boolean isNameUpdated = false;

        String firstName = request.firstName();
        if (firstName != null && !firstName.equals(trainer.getUser().getFirstName())) {
            trainer.getUser().setFirstName(firstName);
            isNameUpdated = true;
        }
        String lastName = request.lastName();
        if (lastName != null && !lastName.equals(trainer.getUser().getLastName())) {
            trainer.getUser().setLastName(lastName);
            isNameUpdated = true;
        }
        if (isNameUpdated) {
            String oldUserName = trainer.getUser().getUsername();
            String numericPart = oldUserName.replaceAll("\\D.*", "");
            trainer.getUser().setUsername(numericPart + calculateUserName(request));
        }
        Specialization specialization = request.specialization();
        if (specialization != null && !specialization.equals(trainer.getSpecialization())) {
            trainer.setSpecialization(specialization);
        }

        return trainer;
    }
}
