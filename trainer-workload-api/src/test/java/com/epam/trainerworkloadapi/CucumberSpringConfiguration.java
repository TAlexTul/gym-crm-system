package com.epam.trainerworkloadapi;

import io.cucumber.java.After;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = TrainerWorkloadApiApplication.class)
@ActiveProfiles("cucumber")
public class CucumberSpringConfiguration {

    @Autowired
    private MongoTemplate mongoTemplate;

    @After
    public void clearDatabase() {
        try {
            mongoTemplate.remove(new Query(), "summaries");
        } catch (Exception e) {
            throw new RuntimeException("Error while clearing database", e);
        }
    }
}
