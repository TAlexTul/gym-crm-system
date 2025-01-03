package com.epam.trainerworkloadapi;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = TrainerWorkloadApiApplication.class)
@ActiveProfiles("cucumber")
public class CucumberSpringConfiguration {
}