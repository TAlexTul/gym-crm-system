package com.epam.gymcrmsystemapi;

import io.cucumber.java.After;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@CucumberContextConfiguration
@SpringBootTest(classes = GymCrmSystemApiApplication.class)
@ActiveProfiles("cucumber")
public class CucumberSpringConfiguration {

    @Autowired
    private DataSource dataSource;

    @After
    public void clearDatabase() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM trainings WHERE 1=1");
            statement.execute("DELETE FROM users WHERE id IN (SELECT user_id FROM trainees)");
            statement.execute("DELETE FROM users WHERE id IN (SELECT user_id FROM trainers)");
        } catch (SQLException e) {
            throw new RuntimeException("Error while clearing database", e);
        }
    }
}
