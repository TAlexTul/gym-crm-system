package com.epam.trainerworkloadapi.model.summary;

import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.user.UserStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document("summaries")
@CompoundIndex(name = "first_last_name_idx", def = "{'first_name': 1, 'last_name': 1}")
public class SummaryTrainingsDuration {

    @Id
    private String id;

    @Field("username")
    private String username;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("user_status")
    private UserStatus userStatus;

    @Field("training_date")
    private List<ProvidedTraining> trainings = new ArrayList<>();

    @Field("summary_duration")
    private Long summaryTrainingsDuration;

    public SummaryTrainingsDuration() {
    }

    public SummaryTrainingsDuration(String id, String username, String firstName,
                                    String lastName, UserStatus userStatus,
                                    List<ProvidedTraining> trainings,
                                    Long summaryTrainingsDuration) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userStatus = userStatus;
        this.trainings = trainings;
        this.summaryTrainingsDuration = summaryTrainingsDuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public List<ProvidedTraining> getTrainings() {
        return trainings;
    }

    public void setTrainings(List<ProvidedTraining> trainings) {
        this.trainings = trainings;
    }

    public Long getSummaryTrainingsDuration() {
        return summaryTrainingsDuration;
    }

    public void setSummaryTrainingsDuration(Long summaryTrainingsDuration) {
        this.summaryTrainingsDuration = summaryTrainingsDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SummaryTrainingsDuration that = (SummaryTrainingsDuration) o;
        return Objects.equals(id, that.id)
                && Objects.equals(username, that.username)
                && Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName)
                && userStatus == that.userStatus
                && Objects.equals(trainings, that.trainings)
                && Objects.equals(summaryTrainingsDuration, that.summaryTrainingsDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, firstName, lastName, userStatus, trainings, summaryTrainingsDuration);
    }
}
