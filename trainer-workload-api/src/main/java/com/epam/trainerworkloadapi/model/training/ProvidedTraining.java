package com.epam.trainerworkloadapi.model.training;

import com.epam.trainerworkloadapi.model.user.User;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "trainings")
public class ProvidedTraining {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "training_date", nullable = false)
    private OffsetDateTime trainingDate;

    @Column(name = "training_duration", nullable = false)
    private Long trainingDuration;

    public ProvidedTraining() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        ProvidedTraining training = (ProvidedTraining) o;
        return Objects.equals(id, training.id)
                && Objects.equals(trainingDate, training.trainingDate)
                && Objects.equals(trainingDuration, training.trainingDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trainingDate, trainingDuration);
    }
}
