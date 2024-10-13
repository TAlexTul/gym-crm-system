package com.epam.gymcrmsystemapi.service.training;

import com.epam.gymcrmsystemapi.exceptions.TraineeExceptions;
import com.epam.gymcrmsystemapi.exceptions.TrainerExceptions;
import com.epam.gymcrmsystemapi.exceptions.TrainingExceptions;
import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.response.TrainerResponse;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.Type;
import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;
import com.epam.gymcrmsystemapi.repository.TraineeRepository;
import com.epam.gymcrmsystemapi.repository.TrainerRepository;
import com.epam.gymcrmsystemapi.repository.TrainingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class TrainingService implements TrainingOperations {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;

    public TrainingService(TraineeRepository traineeRepository,
                           TrainerRepository trainerRepository,
                           TrainingRepository trainingRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
    }

    @Override
    public TrainingResponse create(TrainingSaveRequest request) {
        return TrainingResponse.fromTraining(save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingResponse> list(Pageable pageable) {
        return trainingRepository.findAll(pageable)
                .map(TrainingResponse::fromTrainingWithBasicAttributes);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingResponse> listOf(String traineeUsername, String trainerUsername,
                                         OffsetDateTime fromDate, OffsetDateTime toDate,
                                         Type trainingType, Pageable pageable) {
        Specification<Training> spec = createSpecification(traineeUsername, trainerUsername, fromDate, toDate, trainingType);
        return trainingRepository.findAll(spec, pageable)
                .map(TrainingResponse::fromTrainingWithBasicAttributes);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainerResponse> listOfTrainersNotAssignedByTraineeUsername(String username, Pageable pageable) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> TraineeExceptions.traineeNotFound(username));

        return trainerRepository.findAllNotAssignedToTrainee(trainee, pageable)
                .map(TrainerResponse::fromTrainer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrainingResponse> findById(long id) {
        return trainingRepository.findById(id)
                .map(TrainingResponse::fromTraining);
    }

    @Override
    public TrainingResponse updateTraineeSetOfTrainers(long id, List<String> trainersUsernameList) {
        Set<Trainer> trainers = new HashSet<>();
        for (String username : trainersUsernameList) {
            Trainer trainer = trainerRepository.findByUsername(username)
                    .orElseThrow(() -> TrainerExceptions.trainerNotFound(username));
            trainers.add(trainer);
        }

        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> TrainingExceptions.trainingNotFound(id));
        training.getTrainers().addAll(trainers);

        return TrainingResponse.fromTraining(training);
    }

    private Training save(TrainingSaveRequest request) {
        Trainee trainee = traineeRepository.findById(request.traineeId())
                .orElseThrow(() -> TraineeExceptions.traineeNotFound(request.traineeId()));
        Trainer trainer = trainerRepository.findById(request.trainerId())
                .orElseThrow(() -> TrainerExceptions.trainerNotFound(request.trainerId()));

        var training = new Training();
        training.setTrainees(Set.of(trainee));
        training.setTrainers(Set.of(trainer));
        training.setTrainingName(request.trainingName());
        training.setTrainingTypes(List.of(request.trainingType()));
        training.setTrainingDate(request.trainingDate());
        training.setTrainingDuration(request.trainingDuration());
        return trainingRepository.save(training);
    }

    private Specification<Training> createSpecification(String traineeUsername, String trainerUsername,
                                                        OffsetDateTime fromDate, OffsetDateTime toDate,
                                                        Type trainingType) {
        return Specification
                .where(traineeUserNameLike(traineeUsername))
                .and(trainerUserNameLike(trainerUsername))
                .and(fromDateLike(fromDate))
                .and(toDateLike(toDate))
                .and(trainingTypeLike(trainingType));
    }

    private Specification<Training> traineeUserNameLike(String traineeUserName) {
        return (root, query, criteriaBuilder) ->
                traineeUserName == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(root.get("username"), "%" + traineeUserName + "%");
    }

    private Specification<Training> trainerUserNameLike(String trainerUsername) {
        return (root, query, criteriaBuilder) ->
                trainerUsername == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(root.get("username"), "%" + trainerUsername + "%");
    }

    private Specification<Training> fromDateLike(OffsetDateTime fromDate) {
        return (root, query, criteriaBuilder) ->
                fromDate == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThanOrEqualTo(root.get("trainingDate"), fromDate);
    }

    private Specification<Training> toDateLike(OffsetDateTime toDate) {
        return (root, query, criteriaBuilder) -> {
            if (toDate == null) {
                return criteriaBuilder.conjunction();
            } else {
                return criteriaBuilder.lessThanOrEqualTo(
                        criteriaBuilder.function("DATE_ADD", OffsetDateTime.class,
                                root.get("trainingDate"),
                                criteriaBuilder.literal(root.get("trainingDuration"))
                        ),
                        toDate
                );
            }
        };
    }

    private Specification<Training> trainingTypeLike(Type trainingType) {
        return (root, query, criteriaBuilder) ->
                trainingType == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(root.join("trainingTypes").get("type"), trainingType);
    }
}
