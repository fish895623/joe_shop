package com.bit.joe.shoppingmall;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class DemoApplicationTests {
    private static final String USERNAME = "bit";
    private static final String PASSWORD = "1234";
    private static final String DATABASE_NAME = "shoppingmall";

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        mySQLContainer.start();
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mySQLContainer::getDriverClassName);
    }

    static MySQLContainer<?> mySQLContainer =
            new MySQLContainer<>("mysql:lts")
                    .withUsername(USERNAME)
                    .withPassword(PASSWORD)
                    .withDatabaseName(DATABASE_NAME);

    @Test
    void contextLoads() {
        Assertions.assertTrue(mySQLContainer.isHealthy());
    }
}
