package com.bit.joe.shoppingmall;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class DemoApplicationTests extends ContainerTests {
    @Test
    void contextLoads() {
        Assertions.assertTrue(mySQLContainer.isRunning());
    }
}
