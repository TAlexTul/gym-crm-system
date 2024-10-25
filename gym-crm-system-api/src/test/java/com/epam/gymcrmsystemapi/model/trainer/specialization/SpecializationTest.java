package com.epam.gymcrmsystemapi.model.trainer.specialization;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SpecializationTest {

    @Test
    void constructorAndGetters_shouldInitializeFieldsCorrectly() {
        SpecializationType id = SpecializationType.PERSONAL_TRAINER;
        SpecializationType specializationType = SpecializationType.PERSONAL_TRAINER;

        Specialization specialization = new Specialization(id, specializationType);

        assertEquals(SpecializationType.PERSONAL_TRAINER, specialization.getId());
        assertEquals(SpecializationType.PERSONAL_TRAINER, specialization.getSpecialization());
    }

    @Test
    void setId_shouldUpdateIdAndSpecializationCorrectly() {
        SpecializationType id = SpecializationType.PERSONAL_TRAINER;
        SpecializationType specializationType = SpecializationType.PERSONAL_TRAINER;

        Specialization specialization = new Specialization(id, specializationType);

        SpecializationType newId = SpecializationType.FITNESS_AND_WELLNESS_TRAINER;
        SpecializationType newSpecializationType = SpecializationType.FITNESS_AND_WELLNESS_TRAINER;

        specialization.setId(newId);
        specialization.setSpecialization(newSpecializationType);

        assertEquals(SpecializationType.FITNESS_AND_WELLNESS_TRAINER, specialization.getId());
        assertEquals(SpecializationType.FITNESS_AND_WELLNESS_TRAINER, specialization.getSpecialization());
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        SpecializationType id = SpecializationType.PERSONAL_TRAINER;
        SpecializationType specializationType = SpecializationType.PERSONAL_TRAINER;

        Specialization specialization1 = new Specialization(id, specializationType);
        Specialization specialization2 = new Specialization(id, specializationType);

        assertEquals(specialization1, specialization2);
        assertEquals(specialization1.hashCode(), specialization2.hashCode());

        Specialization specialization3 = new Specialization(SpecializationType.CROSSFIT_COACH, specializationType);
        assertNotEquals(specialization1, specialization3);
    }
}
