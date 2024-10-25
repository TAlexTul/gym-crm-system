package com.epam.gymcrmsystemapi.model.trainer.specialization.response;

import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpecializationResponseTest {

    @Test
    void testFromSpecialization() {
        Specialization specialization = new Specialization(
                SpecializationType.PERSONAL_TRAINER, SpecializationType.PERSONAL_TRAINER);

        SpecializationResponse response = SpecializationResponse.fromSpecialization(specialization);

        assertEquals(SpecializationType.PERSONAL_TRAINER.ordinal(), response.id());
        assertEquals(SpecializationType.PERSONAL_TRAINER, response.specializationType());
    }
}
