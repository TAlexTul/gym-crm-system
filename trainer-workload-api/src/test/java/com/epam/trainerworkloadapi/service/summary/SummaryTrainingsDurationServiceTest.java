package com.epam.trainerworkloadapi.service.summary;

import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsRequest;
import com.epam.trainerworkloadapi.model.MonthlySummaryTrainingsResponse;
import com.epam.trainerworkloadapi.model.summary.SummaryTrainingsDuration;
import com.epam.trainerworkloadapi.model.summary.response.SummaryTrainingsDurationResponse;
import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.training.request.ProvidedTrainingSaveRequest;
import com.epam.trainerworkloadapi.repository.SummaryTrainingsDurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SummaryTrainingsDurationServiceTest {

    @Mock
    private SummaryTrainingsDurationRepository summaryRepository;

    @InjectMocks
    private SummaryTrainingsDurationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate_NewEntry() {
        var request = new ProvidedTrainingSaveRequest(
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                null,
                OffsetDateTime.now(),
                120L
        );

        when(summaryRepository.findByUsername(request.trainerUsername())).thenReturn(Optional.empty());

        SummaryTrainingsDurationResponse response = service.create(request);

        assertEquals(request.trainerUsername(), response.trainerUsername());
        assertEquals(120L, response.summaryTrainingsDuration());
        verify(summaryRepository, times(1)).save(any(SummaryTrainingsDuration.class));
    }

    @Test
    void testCreate_UpdateExistingEntry() {
        var request = new ProvidedTrainingSaveRequest(
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                null,
                OffsetDateTime.now(),
                60L
        );

        var existing = new SummaryTrainingsDuration();
        existing.setUsername("Jane.Jenkins");
        existing.setTrainings(
                new ArrayList<>(List.of(new ProvidedTraining(Year.now(), Month.JANUARY, 30L))));

        when(summaryRepository.findByUsername(request.trainerUsername())).thenReturn(Optional.of(existing));

        SummaryTrainingsDurationResponse response = service.create(request);

        assertEquals(90L, response.summaryTrainingsDuration());
        verify(summaryRepository, times(1)).save(existing);
    }

    @Test
    void testDeleteProvidedTrainings() {
        var trainerUsernames = "Jane.Jenkins";
        var trainingDate = OffsetDateTime.now();
        long trainingDuration = 60L;

        var existing = new SummaryTrainingsDuration();
        existing.setUsername("Jane.Jenkins");
        existing.setTrainings(new ArrayList<>(List.of(
                new ProvidedTraining(Year.from(trainingDate), Month.from(trainingDate), trainingDuration))));

        when(summaryRepository.findByUsername("Jane.Jenkins")).thenReturn(Optional.of(existing));

        service.deleteProvidedTrainings(trainerUsernames, trainingDate, trainingDuration);

        assertTrue(existing.getTrainings().isEmpty());
        verify(summaryRepository, times(1)).save(existing);
    }

    @Test
    void testGetMonthlySummaryTrainingsDuration_NotFound() {
        var request = new MonthlySummaryTrainingsRequest(
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                null,
                YearMonth.parse("2024-12")
        );

        when(summaryRepository.findByUsername(request.trainerUsername())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class, () -> service.getMonthlySummaryTrainingsDuration(request));

        assertEquals("404 NOT_FOUND", exception.getStatusCode().toString());
        assertEquals("Monthly summary trainings duration with user name 'Jane.Jenkins' not found.",
                exception.getReason());
    }

    @Test
    void testGetMonthlySummaryTrainingsDuration_Success() {
        var request = new MonthlySummaryTrainingsRequest(
                "Jane.Jenkins",
                "Jane",
                "Jenkins",
                null,
                YearMonth.parse("2024-12")
        );

        var existing = new SummaryTrainingsDuration();
        existing.setUsername("Jane.Jenkins");
        existing.setSummaryTrainingsDuration(150L);

        when(summaryRepository.findByUsername(request.trainerUsername())).thenReturn(Optional.of(existing));

        MonthlySummaryTrainingsResponse response = service.getMonthlySummaryTrainingsDuration(request);

        assertEquals(150L, response.trainingSummaryDuration());
    }
}
