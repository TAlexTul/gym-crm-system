package com.epam.gymcrmsystemapi.service.trainee;

import com.epam.gymcrmsystemapi.exceptions.TraineeExceptions;
import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainee.request.TraineeSaveMergeRequest;
import com.epam.gymcrmsystemapi.model.trainee.response.TraineeResponse;
import com.epam.gymcrmsystemapi.model.user.OverridePasswordRequest;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import com.epam.gymcrmsystemapi.repository.trainee.TraineeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class TraineeService implements TraineeOperations {

    @Value("${password.length}")
    private int passwordLength;
    @Value("${password.characters}")
    private String passwordCharacters;

    @Autowired
    private TraineeDAO traineeDAO;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public TraineeResponse create(TraineeSaveMergeRequest request) {
        return TraineeResponse.fromTrainee(save(request));
    }

    @Override
    public Page<TraineeResponse> list(Pageable pageable) {
        return traineeDAO.findAll(pageable)
                .map(TraineeResponse::fromTrainee);
    }

    @Override
    public Optional<TraineeResponse> findById(long id) {
        return traineeDAO.findById(id)
                .map(TraineeResponse::fromTrainee);
    }

    @Override
    public TraineeResponse mergeById(long id, TraineeSaveMergeRequest request) {
        Trainee trainee = traineeDAO.findById(id)
                .orElseThrow(() -> TraineeExceptions.traineeNotFound(id));
        return TraineeResponse.fromTrainee(merge(trainee, request));
    }

    @Override
    public TraineeResponse changeStatusById(long id, UserStatus status) {
        Trainee trainee = traineeDAO.findById(id)
                .orElseThrow(() -> TraineeExceptions.traineeNotFound(id));
        if (trainee.getUser().getStatus() != status) {
            trainee.getUser().setStatus(status);
            return TraineeResponse.fromTrainee(traineeDAO.changeStatusById(id, trainee));
        } else {
            return TraineeResponse.fromTrainee(trainee);
        }
    }

    @Override
    public TraineeResponse changePasswordById(long id, OverridePasswordRequest request) {
        Trainee trainee = traineeDAO.findById(id)
                .orElseThrow(() -> TraineeExceptions.traineeNotFound(id));
        trainee.getUser().setPassword(passwordEncoder.encode(request.password()));
        return TraineeResponse.fromTrainee(traineeDAO.changePasswordById(id, trainee));
    }

    @Override
    public void deleteById(long id) {
        traineeDAO.deleteById(id);
    }

    private Trainee save(TraineeSaveMergeRequest request) {
        var trainee = new Trainee();
        trainee.setDateOfBirth(request.dateOfBirth());
        trainee.setAddress(request.address());
        trainee.setUser(getUser(request));
        return traineeDAO.save(trainee);
    }

    private User getUser(TraineeSaveMergeRequest request) {
        var user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUserName(calculateUserName(request));
        user.setPassword(passwordEncoder.encode(generateRandomPassword()));
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private String calculateUserName(TraineeSaveMergeRequest request) {
        boolean isExist = traineeDAO.existByFirstNameAndLastName(request.firstName(), request.lastName());
        if (isExist) {
            Trainee trainee = traineeDAO.findByFirstNameAndLastName(request.firstName(), request.lastName())
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
            String oldUserName = trainee.getUser().getUserName();
            String numericPart = oldUserName.replaceAll("\\D.*", "");
            trainee.getUser().setUserName(numericPart + calculateUserName(request));
        }
        OffsetDateTime dateOfBirth = request.dateOfBirth();
        if (dateOfBirth != null && !dateOfBirth.equals(trainee.getDateOfBirth())) {
            trainee.setDateOfBirth(dateOfBirth);
        }
        String address = request.address();
        if (address != null && !address.equals(trainee.getAddress())) {
            trainee.setAddress(address);
        }
        traineeDAO.mergeById(trainee.getId(), trainee);
        return trainee;
    }
}
