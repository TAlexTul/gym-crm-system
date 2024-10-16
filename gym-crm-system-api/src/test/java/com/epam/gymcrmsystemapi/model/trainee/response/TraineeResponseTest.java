package com.epam.gymcrmsystemapi.model.trainee.response;

import com.epam.gymcrmsystemapi.model.trainee.Trainee;
import com.epam.gymcrmsystemapi.model.user.User;
import com.epam.gymcrmsystemapi.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TraineeResponseTest {

    @Test
    void testFromTrainee() {
        var user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setStatus(UserStatus.ACTIVE);

        var trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);
        trainee.setDateOfBirth(OffsetDateTime.now());
        trainee.setAddress("123 Main St");

        var response = TraineeResponse.fromTrainee(trainee);

        assertEquals(trainee.getUser().getId(), response.userId());
        assertEquals(trainee.getUser().getFirstName(), response.firstName());
        assertEquals(trainee.getUser().getLastName(), response.lastName());
        assertEquals(trainee.getUser().getUsername(), response.userName());
        assertEquals(trainee.getUser().getStatus(), response.status());
        assertEquals(trainee.getId(), response.traineeId());
        assertEquals(trainee.getDateOfBirth(), response.dateOfBirth());
        assertEquals(trainee.getAddress(), response.address());
    }
}
