package com.epam.gymcrmsystemapi.model.training;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "training_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "trainee_id", referencedColumnName = "id")
    )
    private Set<Trainee> trainees = new HashSet<>();

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "training_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id", referencedColumnName = "id")
    )
    private Set<Trainer> trainers;

    @Column(name = "training_name", nullable = false)
    private String trainingName;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_types_id")
    private List<TrainingType> trainingTypes;

    @Column(name = "training_date", nullable = false)
    private OffsetDateTime trainingDate;

    @Column(name = "training_duration", nullable = false)
    private Long trainingDuration;

    public Training() {
    }

    public Training(Long id, Set<Trainee> trainees, Set<Trainer> trainers,
                    String trainingName, List<TrainingType> trainingTypes,
                    OffsetDateTime trainingDate, Long trainingDuration) {
        this.id = id;
        this.trainees = trainees;
        this.trainers = trainers;
        this.trainingName = trainingName;
        this.trainingTypes = trainingTypes;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Trainee> getTrainees() {
        return trainees;
    }

    public void setTrainees(Set<Trainee> trainees) {
        this.trainees = trainees;
    }

    public Set<Trainer> getTrainers() {
        return trainers;
    }

    public void setTrainers(Set<Trainer> trainers) {
        this.trainers = trainers;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public List<TrainingType> getTrainingTypes() {
        return trainingTypes;
    }

    public void setTrainingTypes(List<TrainingType> trainingTypes) {
        this.trainingTypes = trainingTypes;
    }

    public OffsetDateTime getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(OffsetDateTime trainingDate) {
        this.trainingDate = trainingDate;
    }

    public Long getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Long trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Training training = (Training) o;
        return Objects.equals(id, training.id)
                && Objects.equals(trainingName, training.trainingName)
                && Objects.equals(trainingDate, training.trainingDate)
                && Objects.equals(trainingDuration, training.trainingDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trainingName, trainingDate, trainingDuration);
    }
}
