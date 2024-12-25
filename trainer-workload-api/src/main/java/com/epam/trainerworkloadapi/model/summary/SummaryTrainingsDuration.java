package com.epam.trainerworkloadapi.model.summary;

import com.epam.trainerworkloadapi.model.training.ProvidedTraining;
import com.epam.trainerworkloadapi.model.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document("summaries")
@CompoundIndex(name = "first_last_name_idx", def = "{'first_name': 1, 'last_name': 1}")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
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

}
