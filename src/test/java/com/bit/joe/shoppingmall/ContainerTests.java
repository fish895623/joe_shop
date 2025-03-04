package com.bit.joe.shoppingmall;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.bit.joe.shoppingmall.config.MySQLContainerConfig;

@SpringBootTest
@Testcontainers
public class ContainerTests {
    static final MySQLContainer<?> mySQLContainer = MySQLContainerConfig.getInstance();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        mySQLContainer.start();
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", mySQLContainer::getDriverClassName);
    }

    @Test
    void contextLoads() {
        Assertions.assertTrue(mySQLContainer.isRunning());
    }
}
