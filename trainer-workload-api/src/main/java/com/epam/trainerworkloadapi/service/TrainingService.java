package com.epam.trainerworkloadapi.service;

import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsRequest;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsResponse;
import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.trainerworkloadapi.model.training.response.ProvidedTrainingResponse;
import com.epam.trainerworkloadapi.model.user.User;
import com.epam.trainerworkloadapi.repository.TrainingRepository;
import com.epam.trainerworkloadapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Transactional
public class TrainingService implements TrainingOperations {

    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;

    public TrainingService(TrainingRepository trainingRepository,
                           UserRepository userRepository) {
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ProvidedTrainingResponse create(ProvidedTrainingSaveRequest request) {
        return ProvidedTrainingResponse.fromProvidedTraining(save(request));
    }

    @Override
    public void deleteProvidedTrainings(String trainerUsernames, OffsetDateTime trainingDate, long trainingDuration) {
        if (trainerUsernames == null || trainerUsernames.isBlank()) return;

        String[] usernames = trainerUsernames.split(",");
        for (String trainerUsername : usernames) {
            if (trainerUsername.isBlank()) continue;
            delete(trainerUsername, trainingDate, trainingDuration);
        }
    }

    @Override
    public void deleteTrainingsByYearMonth(YearMonth yearMonth) {
        ZoneOffset offset = ZoneOffset.UTC;
        OffsetDateTime startDate = yearMonth.atDay(1).atStartOfDay().atOffset(offset);
        OffsetDateTime endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay().atOffset(offset);

        trainingRepository.deleteByTrainingDateBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlySummaryTrainingsResponse findSummaryTrainings(MonthlySummaryTrainingsRequest request) {
        List<ProvidedTraining> trainings = getProvidedTrainings(request);

        if (trainings.isEmpty()) return MonthlySummaryTrainingsResponse.fromEmptyListProvidedTraining(request);
        return MonthlySummaryTrainingsResponse.fromListProvidedTraining(trainings);
    }

    private ProvidedTraining save(ProvidedTrainingSaveRequest request) {
        User user = getUser(request);

        var training = new ProvidedTraining();
        training.setUser(user);
        training.setTrainingDate(request.trainingDate());
        training.setTrainingDuration(request.trainingDuration());
        trainingRepository.save(training);

        return training;
    }

    private User getUser(ProvidedTrainingSaveRequest request) {
        return userRepository.findByUsername(request.trainerUsername())
                .orElseGet(() -> {
                    var user = new User();
                    user.setUsername(request.trainerUsername());
                    user.setFirstName(request.trainerFirstName());
                    user.setLastName(request.trainerLastName());
                    user.setStatus(request.trainerStatus());
                    userRepository.save(user);

                    return user;
                });
    }

    private void delete(String trainerUsername, OffsetDateTime trainingDate, long trainingDuration) {
        var training = trainingRepository.findByUserUsernameAndTrainingDateAndTrainingDuration(
                trainerUsername, trainingDate, trainingDuration);

        training.ifPresent(trainingRepository::delete);
    }

    private List<ProvidedTraining> getProvidedTrainings(MonthlySummaryTrainingsRequest request) {
        return trainingRepository.findByUserUsernameAndMonth(
                request.trainerUsername(), request.yearMonth().getYear(), request.yearMonth().getMonthValue());
    }
}
