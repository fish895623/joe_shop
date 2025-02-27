package com.bit.joe.shoppingmall;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class DemoApplicationTests {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String DATABASE_NAME = "testdb";

    static MySQLContainer<?> mySQLContainer =
            new MySQLContainer<>("mysql:8.0")
                    .withUsername(USERNAME)
                    .withPassword(PASSWORD)
                    .withDatabaseName(DATABASE_NAME);

    @Test
    void contextLoads() {}
}
