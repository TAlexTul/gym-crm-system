package com.epam.gymcrmsystemapi.service.trainer;

import com.epam.gymcrmsystemapi.exceptions.TraineeExceptions;
import com.epam.gymcrmsystemapi.exceptions.TrainerExceptions;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.request.TrainerSaveMergeRequest;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.user.OverridePasswordRequest;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.trainer.TrainerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class TrainerService implements TrainerOperations {

    @Value("${password.length}")
    private int passwordLength;
    @Value("${password.characters}")
    private String passwordCharacters;

    @Autowired
    private TrainerDAO trainerDAO;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TrainerResponse create(TrainerSaveMergeRequest request) {
        return TrainerResponse.fromTrainer(save(request));
    }

    @Override
    public Page<TrainerResponse> list(Pageable pageable) {
        return trainerDAO.findAll(pageable)
                .map(TrainerResponse::fromTrainer);
    }

    @Override
    public Optional<TrainerResponse> findById(long id) {
        return trainerDAO.findById(id)
                .map(TrainerResponse::fromTrainer);
    }

    @Override
    public TrainerResponse mergeById(long id, TrainerSaveMergeRequest request) {
        Trainer trainer = trainerDAO.findById(id)
                .orElseThrow(() -> TrainerExceptions.trainerNotFound(id));
        return TrainerResponse.fromTrainer(merge(trainer, request));
    }

    @Override
    public TrainerResponse changeStatusById(long id, UserStatus status) {
        Trainer trainer = trainerDAO.findById(id)
                .orElseThrow(() -> TraineeExceptions.traineeNotFound(id));
        if (trainer.getUser().getStatus() != status) {
            trainer.getUser().setStatus(status);
            return TrainerResponse.fromTrainer(trainerDAO.changeById(id, trainer));
        } else {
            return TrainerResponse.fromTrainer(trainer);
        }
    }

    @Override
    public TrainerResponse changePasswordById(long id, OverridePasswordRequest request) {
        Trainer trainer = trainerDAO.findById(id)
                .orElseThrow(() -> TrainerExceptions.trainerNotFound(id));
        trainer.getUser().setPassword(passwordEncoder.encode(request.password()));
        return TrainerResponse.fromTrainer(trainerDAO.changeById(id, trainer));
    }

    @Override
    public void deleteById(long id) {
        trainerDAO.deleteById(id);
    }

    private Trainer save(TrainerSaveMergeRequest request) {
        var trainer = new Trainer();
        trainer.setSpecialization(request.specialization());
        trainer.setUser(getUser(request));
        return trainerDAO.save(trainer);
    }

    private User getUser(TrainerSaveMergeRequest request) {
        var user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUserName(calculateUserName(request));
        user.setPassword(passwordEncoder.encode(generateRandomPassword()));
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private String calculateUserName(TrainerSaveMergeRequest request) {
        boolean isExist = trainerDAO.existByFirstNameAndLastName(request.firstName(), request.lastName());
        if (isExist) {
            Trainer trainer = trainerDAO.findByFirstNameAndLastName(request.firstName(), request.lastName())
                    .orElseThrow(() -> TraineeExceptions.traineeNotFound(request.firstName(), request.lastName()));
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
            String oldUserName = trainer.getUser().getUserName();
            String numericPart = oldUserName.replaceAll("\\D.*", "");
            trainer.getUser().setUserName(numericPart + calculateUserName(request));
        }
        String specialization = request.specialization();
        if (specialization != null && !specialization.equals(trainer.getSpecialization())) {
            trainer.setSpecialization(specialization);
        }
        trainerDAO.changeById(trainer.getId(), trainer);
        return trainer;
    }
}
