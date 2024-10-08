package com.epam.gymcrmsystemapi.model.trainer;

import com.epam.gymcrmsystemapi.model.user.User;

import java.util.Objects;

public class Trainer extends User {

    private Long id;
    private String specialization;
    private User user;

    public Trainer() {
    }

    public Trainer(Long id, String specialization, User user) {
        this.id = id;
        this.specialization = specialization;
        this.user = user;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Trainer trainer = (Trainer) o;
        return Objects.equals(id, trainer.id)
                && Objects.equals(specialization, trainer.specialization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, specialization);
    }
}
