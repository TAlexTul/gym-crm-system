package com.epam.gymcrmsystemapi.service.trainingtype;

import com.epam.gymcrmsystemapi.model.training.type.TrainingType;
import com.epam.gymcrmsystemapi.model.training.type.Type;
import com.epam.gymcrmsystemapi.model.training.type.response.TrainingTypeResponse;
import com.epam.gymcrmsystemapi.repository.TrainingTypeRepository;
import com.epam.gymcrmsystemapi.service.trainingtype.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class TrainingTypeServiceTest {

    @InjectMocks
    private TrainingTypeService trainingTypeService;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testList() {

        List<TrainingType> list = getTrainingType();

        when(trainingTypeRepository.findAll()).thenReturn(list);

        List<TrainingTypeResponse> responses = trainingTypeService.list();
        assertNotNull(responses);
        assertEquals(list.get(0).getId().ordinal(), responses.get(0).id());
        assertEquals(list.get(0).getType(), responses.get(0).type());
    }

    private List<TrainingType> getTrainingType() {
        return List.of(
                new TrainingType(Type.STRENGTH_TRAINING, Type.STRENGTH_TRAINING),
                new TrainingType(Type.CARDIO_WORKOUT, Type.CARDIO_WORKOUT),
                new TrainingType(Type.FUNCTIONAL_TRAINING, Type.FUNCTIONAL_TRAINING),
                new TrainingType(Type.CROSSFIT_WORKOUT, Type.CROSSFIT_WORKOUT),
                new TrainingType(Type.PILATES_SESSION, Type.PILATES_SESSION),
                new TrainingType(Type.BODYBUILDING_PROGRAM, Type.BODYBUILDING_PROGRAM),
                new TrainingType(Type.MARTIAL_ARTS_TRAINING, Type.MARTIAL_ARTS_TRAINING),
                new TrainingType(Type.SWIMMING_SESSION, Type.SWIMMING_SESSION),
                new TrainingType(Type.GROUP_FITNESS_CLASS, Type.GROUP_FITNESS_CLASS),
                new TrainingType(Type.FITNESS_AEROBICS, Type.FITNESS_AEROBICS),
                new TrainingType(Type.REHABILITATION_WORKOUT, Type.REHABILITATION_WORKOUT),
                new TrainingType(Type.NUTRITION_AND_DIET_PLAN, Type.NUTRITION_AND_DIET_PLAN),
                new TrainingType(Type.CYCLING_WORKOUT, Type.CYCLING_WORKOUT),
                new TrainingType(Type.GYMNASTICS_TRAINING, Type.GYMNASTICS_TRAINING),
                new TrainingType(Type.TRX_TRAINING, Type.TRX_TRAINING),
                new TrainingType(Type.SPECIAL_NEEDS_TRAINING, Type.SPECIAL_NEEDS_TRAINING),
                new TrainingType(Type.STRETCHING_SESSION, Type.STRETCHING_SESSION),
                new TrainingType(Type.BOOTCAMP_WORKOUT, Type.BOOTCAMP_WORKOUT)
        );
    }
}
