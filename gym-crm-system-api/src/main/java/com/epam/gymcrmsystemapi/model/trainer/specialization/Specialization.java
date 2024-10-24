package com.epam.gymcrmsystemapi.model.trainer.specialization;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "specializations")
public class Specialization {

    @Id
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.ORDINAL)
    private SpecializationType id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SpecializationType specializationType;

    public Specialization() {
    }

    public Specialization(SpecializationType id,
                          SpecializationType specializationType) {
        this.id = id;
        this.specializationType = specializationType;
    }

    public SpecializationType getId() {
        return id;
    }

    public void setId(SpecializationType id) {
        this.id = id;
    }

    public SpecializationType getSpecialization() {
        return specializationType;
    }

    public void setSpecialization(SpecializationType specializationType) {
        this.specializationType = specializationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Specialization specialization = (Specialization) o;
        return id == specialization.id && specializationType == specialization.specializationType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, specializationType);
    }
}
