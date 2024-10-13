package com.epam.gymcrmsystemapi.model.trainer.response;

import com.epam.gymcrmsystemapi.model.trainer.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainerResponseTest {

    @Test
    void testFromTrainer() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("Alex");
        user.setLastName("Brown");
        user.setUsername("Alex.Brown");
        user.setStatus(UserStatus.ACTIVE);

        var trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(user);
        trainer.setSpecialization(Specialization.PERSONAL_TRAINER);

        var response = TrainerResponse.fromTrainer(trainer);

        assertEquals(trainer.getUser().getId(), response.userId());
        assertEquals(trainer.getUser().getFirstName(), response.firstName());
        assertEquals(trainer.getUser().getLastName(), response.lastName());
        assertEquals(trainer.getUser().getUsername(), response.userName());
        assertEquals(trainer.getUser().getStatus(), response.status());
        assertEquals(trainer.getId(), response.trainerId());
        assertEquals(trainer.getSpecialization(), response.specialization());
    }
}
