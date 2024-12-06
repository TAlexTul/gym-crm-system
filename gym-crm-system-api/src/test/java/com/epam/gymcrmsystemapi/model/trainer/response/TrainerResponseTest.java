package com.epam.gymcrmsystemapi.model.trainer.response;

import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrainerResponseTest {

    @Test
    void testFromTrainer() {
        Trainer trainer = getTrainer();
        var response = TrainerResponse.fromTrainer(trainer);

        assertEquals(trainer.getUser().getUsername(), response.username());
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getStatus(), response.status());

        if (trainer.getSpecialization().getId() != null) {
            assertEquals(trainer.getSpecialization().getId().ordinal(), response.specialization().id());
        } else {
            assertNull(response.specialization().id());
        }

        assertEquals(trainer.getSpecialization().getSpecialization(), response.specialization().specializationType());
    }

    private Trainer getTrainer() {
        User user = getUser();
        var specialization = new Specialization(
                SpecializationType.PERSONAL_TRAINER, SpecializationType.PERSONAL_TRAINER);
        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setSpecialization(specialization);
        trainer.setUser(user);
        return trainer;
    }

    private User getUser() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("Jane");
        user.setLastName("Jenkins");
        user.setUsername("Jane.Jenkins");
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }
}
