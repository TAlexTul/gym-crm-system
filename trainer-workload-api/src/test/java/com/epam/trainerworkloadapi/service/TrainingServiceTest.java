package com.epam.trainerworkloadapi.service;

import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsRequest;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsResponse;
import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.trainerworkloadapi.model.training.response.ProvidedTrainingResponse;
import com.epam.trainerworkloadapi.model.user.User;
import com.epam.trainerworkloadapi.model.user.UserStatus;
import com.epam.trainerworkloadapi.repository.TrainingRepository;
import com.epam.trainerworkloadapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TrainingService trainingService;

    private User mockUser;
    private ProvidedTrainingSaveRequest mockSaveRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User();
        mockUser.setUsername("Jane.Jenkins");
        mockUser.setFirstName("Jane");
        mockUser.setLastName("Jenkins");
        mockUser.setStatus(UserStatus.ACTIVE);

        mockSaveRequest = new ProvidedTrainingSaveRequest(
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                UserStatus.ACTIVE,
                OffsetDateTime.now(),
                30000L
        );
    }

    @Test
    void testCreate() {
        ProvidedTraining mockTraining = new ProvidedTraining();
        mockTraining.setUser(mockUser);
        mockTraining.setTrainingDate(mockSaveRequest.trainingDate());
        mockTraining.setTrainingDuration(mockSaveRequest.trainingDuration());

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));
        when(trainingRepository.save(any(ProvidedTraining.class))).thenReturn(mockTraining);

        ProvidedTrainingResponse response = trainingService.create(mockSaveRequest);

        assertNotNull(response);
        assertEquals(mockSaveRequest.trainingDuration(), response.trainingDuration());
        verify(trainingRepository, times(1)).save(any(ProvidedTraining.class));
    }

    @Test
    void testDeleteProvidedTrainings() {
        OffsetDateTime trainingDate = OffsetDateTime.now();
        long trainingDuration = 30000L;

        when(trainingRepository.findByUserUsernameAndTrainingDateAndTrainingDuration(anyString(), any(), anyLong()))
                .thenReturn(Optional.of(new ProvidedTraining()));

        trainingService.deleteProvidedTrainings("Jane.Jenkins", trainingDate, trainingDuration);

        verify(trainingRepository, times(1)).delete(any(ProvidedTraining.class));
    }

    @Test
    void testDeleteTrainingsByYearMonth() {
        YearMonth yearMonth = YearMonth.of(2024, 1);
        ZoneOffset offset = ZoneOffset.UTC;
        OffsetDateTime startDate = yearMonth.atDay(1).atStartOfDay().atOffset(offset);
        OffsetDateTime endDate = yearMonth.plusMonths(1).atDay(1).atStartOfDay().atOffset(offset);

        trainingService.deleteTrainingsByYearMonth(yearMonth);

        verify(trainingRepository, times(1)).deleteByTrainingDateBetween(startDate, endDate);
    }

    @Test
    void testFindSummaryTrainings_NoTrainings() {
        MonthlySummaryTrainingsRequest request = new MonthlySummaryTrainingsRequest(
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                UserStatus.ACTIVE,
                YearMonth.of(2024, 1)
        );

        when(trainingRepository.findByUserUsernameAndMonth(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());

        MonthlySummaryTrainingsResponse response = trainingService.findSummaryTrainings(request);

        assertNotNull(response);
        assertEquals(request.trainerUsername(), response.trainerUsername());
        assertEquals(request.trainerFirstName(), response.trainerFirstName());
        assertEquals(request.trainerLastName(), response.trainerLastName());
        assertEquals(request.trainerStatus(), response.trainerStatus());
        assertNull(response.trainings());
    }

    @Test
    void testFindSummaryTrainings_WithTrainings() {
        MonthlySummaryTrainingsRequest request = new MonthlySummaryTrainingsRequest(
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                UserStatus.ACTIVE,
                YearMonth.of(2024, 1)
        );

        ProvidedTraining mockTraining = new ProvidedTraining();
        mockTraining.setTrainingDate(OffsetDateTime.now());
        mockTraining.setTrainingDuration(30000L);
        mockTraining.setUser(mockUser);

        when(trainingRepository.findByUserUsernameAndMonth(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(mockTraining));

        MonthlySummaryTrainingsResponse response = trainingService.findSummaryTrainings(request);

        assertNotNull(response);
        assertEquals(request.trainerUsername(), response.trainerUsername());
        assertEquals(request.trainerFirstName(), response.trainerFirstName());
        assertEquals(request.trainerLastName(), response.trainerLastName());
        assertEquals(request.trainerStatus(), response.trainerStatus());
        assertFalse(response.trainings().isEmpty());
        assertEquals(1, response.trainings().size());
        assertEquals(30000L, response.trainingSummaryDuration());
    }
}
