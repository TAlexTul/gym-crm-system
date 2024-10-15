package com.epam.gymcrmsystemapi.model.trainee;

import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.training.Training;
import com.epam.gymcrmsystemapi.model.user.User;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "trainees")
public class Trainee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_of_birth")
    private OffsetDateTime dateOfBirth;

    @Column
    private String address;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private User user;

    @ManyToMany(mappedBy = "trainees")
    private Set<Training> trainings;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "trainee_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id", referencedColumnName = "id")
    )
    private Set<Trainer> trainers;

    public Trainee() {
    }

    public Trainee(Long id, OffsetDateTime dateOfBirth, String address, User user,
                   Set<Training> trainings, Set<Trainer> trainers) {
        this.id = id;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.user = user;
        this.trainings = trainings;
        this.trainers = trainers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OffsetDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(OffsetDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(Set<Training> trainings) {
        this.trainings = trainings;
    }

    public Set<Trainer> getTrainers() {
        return trainers;
    }

    public void setTrainers(Set<Trainer> trainers) {
        this.trainers = trainers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trainee trainee = (Trainee) o;
        return Objects.equals(id, trainee.id)
                && Objects.equals(dateOfBirth, trainee.dateOfBirth)
                && Objects.equals(address, trainee.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateOfBirth, address);
    }
}
