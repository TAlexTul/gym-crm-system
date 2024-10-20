package com.epam.gymcrmsystemapi.model.training.type;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "training_types")
public class TrainingType {

    @Id
    @Enumerated(EnumType.ORDINAL)
    private Type id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    public TrainingType() {
    }

    public TrainingType(Type id, Type type) {
        this.id = id;
        this.type = type;
    }

    public Type getId() {
        return id;
    }

    public void setId(Type id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainingType that = (TrainingType) o;
        return Objects.equals(id, that.id) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
