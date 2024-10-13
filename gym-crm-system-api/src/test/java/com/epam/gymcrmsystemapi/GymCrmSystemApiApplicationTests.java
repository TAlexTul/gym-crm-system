package com.epam.gymcrmsystemapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "file:../.env")
class GymCrmSystemApiApplicationTests {

    @Test
    void contextLoads() {
    }

}
