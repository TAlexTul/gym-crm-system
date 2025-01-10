package com.epam.gymcrmsystemapi.service.training;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.training.request.TrainingSaveRequest;
import com.epam.gymcrmsystemapi.model.training.response.TrainingResponse;
import com.epam.gymcrmsystemapi.model.training.type.TrainingType;
import com.epam.gymcrmsystemapi.model.training.type.Type;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.repository.TraineeRepository;
import com.epam.gymcrmsystemapi.repository.TrainerRepository;
import com.epam.gymcrmsystemapi.repository.TrainingRepository;
import com.epam.gymcrmsystemapi.repository.TrainingTypeRepository;
import jakarta.persistence.criteria.Join;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class TrainingService implements TrainingOperations {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingRepository trainingRepository;

    public TrainingService(TraineeRepository traineeRepository,
                           TrainerRepository trainerRepository,
                           TrainingTypeRepository trainingTypeRepository,
                           TrainingRepository trainingRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
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
    public Page<TrainingResponse> filterBy(String traineeUsername, String trainerUsername,
                                           OffsetDateTime fromDate, OffsetDateTime toDate,
                                           Type trainingType, Pageable pageable) {
        Specification<Training> spec = createSpecification(
                traineeUsername, trainerUsername, fromDate, toDate, trainingType);
        return trainingRepository.findAll(spec, pageable)
                .map(TrainingResponse::fromTrainingWithBasicAttributes);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrainingResponse> findById(long id) {
        return trainingRepository.findById(id)
                .map(TrainingResponse::fromTraining);
    }

    @Override
    public Optional<TrainingResponse> deleteById(long id) {
        Optional<Training> training = trainingRepository.findById(id);
        training.ifPresent(trainingRepository::delete);

        return training.map(TrainingResponse::fromTraining);
    }

    private Training save(TrainingSaveRequest request) {
        Optional<Trainee> trainee = traineeRepository.findByUsername(request.traineeUsername());
        Optional<Trainer> trainer = trainerRepository.findByUsername(request.trainerUsername());
        Type trainingTypeName = request.trainingType();
        Optional<TrainingType> trainingType = Optional.empty();
        if (trainingTypeName != null) {
            trainingType = trainingTypeRepository.findById(trainingTypeName);
        }

        Training training = trainingRepository.findByTrainingNameLike(request.trainingName())
                .orElseGet(() -> {
                    var newTraining = new Training();
                    newTraining.setTrainingName(request.trainingName());
                    newTraining.setTrainingDate(request.trainingDate());
                    newTraining.setTrainingDuration(request.trainingDuration());
                    return newTraining;
                });

        Set<Trainee> trainees = training.getTrainees();
        Set<Trainer> trainers = training.getTrainers();
        List<TrainingType> trainingTypes = training.getTrainingTypes();

        trainee.ifPresent(trainees::add);
        trainer.ifPresent(trainers::add);
        trainingType.ifPresent(type -> {
            if (!trainingTypes.contains(type)) trainingTypes.add(type);
        });

        return trainingRepository.save(training);
    }

    private Specification<Training> createSpecification(String traineeUsername, String trainerUsername,
                                                        OffsetDateTime fromDate, OffsetDateTime toDate,
                                                        Type trainingType) {
        return Specification
                .where(traineeUserNameLike(traineeUsername))
                .and(trainerUserNameLike(trainerUsername))
                .and(trainingStartDateAfterOrEqualTo(fromDate))
                .and(trainingEndDateBeforeOrEqualTo(toDate))
                .and(trainingTypeLike(trainingType));
    }

    private Specification<Training> traineeUserNameLike(String traineeUsername) {
        return (root, query, criteriaBuilder) -> {
            if (traineeUsername == null) return criteriaBuilder.conjunction();
            Join<Training, Trainee> traineeJoin = root.join("trainees");
            Join<Trainee, User> userJoin = traineeJoin.join("user");
            return criteriaBuilder.like(userJoin.get("username"), "%" + traineeUsername + "%");
        };
    }

    private Specification<Training> trainerUserNameLike(String trainerUsername) {
        return (root, query, criteriaBuilder) -> {
            if (trainerUsername == null) return criteriaBuilder.conjunction();
            Join<Training, Trainer> trainerJoin = root.join("trainers");
            Join<Trainer, User> userJoin = trainerJoin.join("user");
            return criteriaBuilder.like(userJoin.get("username"), "%" + trainerUsername + "%");
        };
    }

    private Specification<Training> trainingStartDateAfterOrEqualTo(OffsetDateTime fromDate) {
        return (root, query, criteriaBuilder) ->
                fromDate == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThanOrEqualTo(root.get("trainingDate"), fromDate);
    }

    private Specification<Training> trainingEndDateBeforeOrEqualTo(OffsetDateTime toDate) {
        return (root, query, criteriaBuilder) -> {
            if (toDate == null) {
                return criteriaBuilder.conjunction();
            } else {
                return criteriaBuilder.lessThanOrEqualTo(
                        criteriaBuilder.function("training_date + interval '1 second' *", OffsetDateTime.class,
                                root.get("trainingDuration")
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
