package com.bit.joe.shoppingmall;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = BitApplication.class)
@Testcontainers
class BitApplicationTests {
    @Container
    public static MySQLContainer<?> mySQLContainer =
            new MySQLContainer<>("mysql:5.7")
                    .withExposedPorts(3306)
                    .withDatabaseName("shoppingmall")
                    .withUsername("bit")
                    .withPassword("1234");

    @Test
    void contextLoads() {
        assert mySQLContainer.isRunning();
    }
}
