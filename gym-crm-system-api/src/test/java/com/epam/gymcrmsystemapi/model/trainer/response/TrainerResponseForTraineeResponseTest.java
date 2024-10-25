package com.epam.gymcrmsystemapi.model.trainer.response;

import com.epam.gymcrmsystemapi.model.trainer.Trainer;
import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;
import com.epam.gymcrmsystemapi.model.trainer.specialization.response.SpecializationResponse;
import com.epam.gymcrmsystemapi.model.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TrainerResponseForTraineeResponseTest {

    @Test
    void testFromTrainerForTraineeResponse() {
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("John.Doe");
        when(mockUser.getFirstName()).thenReturn("John");
        when(mockUser.getLastName()).thenReturn("Doe");

        Specialization mockSpecialization = mock(Specialization.class);
        when(mockSpecialization.getId()).thenReturn(SpecializationType.PERSONAL_TRAINER);
        when(mockSpecialization.getSpecialization()).thenReturn(SpecializationType.PERSONAL_TRAINER);
        Trainer mockTrainer = mock(Trainer.class);
        when(mockTrainer.getUser()).thenReturn(mockUser);
        when(mockTrainer.getSpecialization()).thenReturn(mockSpecialization);

        SpecializationResponse expectedSpecializationResponse =
                SpecializationResponse.fromSpecialization(mockSpecialization);

        TrainerResponseForTraineeResponse response =
                TrainerResponseForTraineeResponse.fromTrainerForTraineeResponse(mockTrainer);

        assertNotNull(response);
        assertEquals("John.Doe", response.username());
        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
        assertEquals(expectedSpecializationResponse, response.specialization());

        verify(mockUser).getUsername();
        verify(mockUser).getFirstName();
        verify(mockUser).getLastName();
        verify(mockTrainer).getSpecialization();
        verify(mockSpecialization, times(2)).getId();
        verify(mockSpecialization, times(2)).getSpecialization();
    }
}
