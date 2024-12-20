package com.epam.trainerworkloadapi.service.summary;

import com.epam.trainerworkloadapi.exceptions.MonthlySummaryTrainingsExceptions;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsRequest;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsResponse;
import com.epam.trainerworkloadapi.model.summary.SummaryTrainingsDuration;
import com.epam.trainerworkloadapi.model.summary.response.SummaryTrainingsDurationResponse;
import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.trainerworkloadapi.repository.SummaryTrainingsDurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SummaryTrainingsDurationService implements SummaryOperations {

    private final SummaryTrainingsDurationRepository summaryRepository;

    public SummaryTrainingsDurationService(SummaryTrainingsDurationRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    @Override
    public SummaryTrainingsDurationResponse create(ProvidedTrainingSaveRequest request) {
        Optional<SummaryTrainingsDuration> stdByUsername =
                summaryRepository.findByUsername(request.trainerUsername());

        if (stdByUsername.isPresent()) {
            SummaryTrainingsDuration std = stdByUsername.get();
            return SummaryTrainingsDurationResponse.formSummaryTrainingsDuration(update(request, std));
        } else {
            return SummaryTrainingsDurationResponse.formSummaryTrainingsDuration(save(request));
        }
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
    public MonthlySummaryTrainingsResponse getMonthlySummaryTrainingsDuration(MonthlySummaryTrainingsRequest request) {
        SummaryTrainingsDuration std = summaryRepository.findByUsername(request.trainerUsername())
                .orElseThrow(() -> MonthlySummaryTrainingsExceptions.monthlySummaryTrainingsDurationNotFound(
                        request.trainerUsername()));

        return MonthlySummaryTrainingsResponse.fromSummaryTrainingsDuration(std);
    }

    private SummaryTrainingsDuration update(ProvidedTrainingSaveRequest request,
                                            SummaryTrainingsDuration std) {
        List<ProvidedTraining> providedTrainings = std.getTrainings();
        providedTrainings.addAll(getProvidedTrainings(request));
        std.setTrainings(providedTrainings);
        long summaryTrainingsDuration = getSummaryTrainingsDuration(providedTrainings);
        std.setSummaryTrainingsDuration(summaryTrainingsDuration);
        summaryRepository.save(std);

        return std;
    }

    private SummaryTrainingsDuration save(ProvidedTrainingSaveRequest request) {
        List<ProvidedTraining> providedTrainings = getProvidedTrainings(request);
        long summaryTrainingsDuration = getSummaryTrainingsDuration(providedTrainings);

        var std = new SummaryTrainingsDuration();
        std.setFirstName(request.trainerFirstName());
        std.setLastName(request.trainerLastName());
        std.setUsername(request.trainerUsername());
        std.setUserStatus(request.trainerStatus());
        std.setTrainings(providedTrainings);
        std.setSummaryTrainingsDuration(summaryTrainingsDuration);
        summaryRepository.save(std);

        return std;
    }

    private List<ProvidedTraining> getProvidedTrainings(ProvidedTrainingSaveRequest request) {
        return new ArrayList<>(List.of(
                new ProvidedTraining(
                        Year.from(request.trainingDate()),
                        Month.from(request.trainingDate()),
                        request.trainingDuration()
                )));
    }

    private long getSummaryTrainingsDuration(List<ProvidedTraining> providedTrainings) {
        return providedTrainings.stream()
                .mapToLong(ProvidedTraining::getTrainingDuration)
                .sum();
    }

    private void delete(String trainerUsername, OffsetDateTime trainingDate, long trainingDuration) {
        Optional<SummaryTrainingsDuration> stdByUsername = summaryRepository.findByUsername(trainerUsername);
        if (stdByUsername.isPresent()) {
            SummaryTrainingsDuration std = stdByUsername.get();
            List<ProvidedTraining> providedTrainings = std.getTrainings();
            Optional<ProvidedTraining> providedTraining = providedTrainings.stream()
                    .filter(s -> s.getYear().equals(Year.from(trainingDate))
                            && s.getMonth().equals(Month.from(trainingDate))
                            && s.getTrainingDuration() == trainingDuration)
                    .findFirst();
            providedTraining.ifPresent(providedTrainings::remove);
            long summaryTrainingsDuration = getSummaryTrainingsDuration(providedTrainings);
            std.setSummaryTrainingsDuration(summaryTrainingsDuration);

            summaryRepository.save(std);
        }
    }
}
