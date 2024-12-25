package com.epam.trainerworkloadapi.model.training;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Month;
import java.time.Year;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProvidedTraining {

    private Year year;

    private Month month;

    private long trainingDuration;

}
