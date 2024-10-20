package com.epam.gymcrmsystemapi.model.trainer.specialization.response;

import com.epam.gymcrmsystemapi.model.trainer.specialization.Specialization;
import com.epam.gymcrmsystemapi.model.trainer.specialization.SpecializationType;

public record SpecializationResponse(Integer id,
                                     SpecializationType specializationType) {

    public static SpecializationResponse fromSpecialization(Specialization specialization) {
        return new SpecializationResponse(
                specialization.getId().ordinal(),
                specialization.getSpecialization()
        );
    }
}
