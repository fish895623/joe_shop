package com.bit.joe.shoppingmall;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@PropertySource("classpath:application.test.properties")
class DemoApplicationTests {
    private static final String USERNAME = "bit";
    private static final String PASSWORD = "1234";
    private static final String DATABASE_NAME = "shoppingmall";

    static MySQLContainer<?> mySQLContainer =
            new MySQLContainer<>("mysql:8.0")
                    .withUsername(USERNAME)
                    .withPassword(PASSWORD)
                    .withDatabaseName(DATABASE_NAME);

    @Test
    void contextLoads() {}
}
