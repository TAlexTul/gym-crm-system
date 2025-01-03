package com.epam.gymcrmsystemapi;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = GymCrmSystemApiApplication.class)
@ActiveProfiles("cucumber")
public class CucumberSpringConfiguration {
}
