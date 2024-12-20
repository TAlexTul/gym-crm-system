package com.epam.trainerworkloadapi.model.training;

import java.time.Month;
import java.time.Year;
import java.util.Objects;

public class ProvidedTraining {

    private Year year;

    private Month month;

    private long trainingDuration;

    public ProvidedTraining() {
    }

    public ProvidedTraining(Year year, Month month,
                            long trainingDuration) {
        this.year = year;
        this.month = month;
        this.trainingDuration = trainingDuration;
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public long getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(long trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProvidedTraining training = (ProvidedTraining) o;
        return trainingDuration == training.trainingDuration
                && Objects.equals(year, training.year)
                && month == training.month;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, trainingDuration);
    }
}
