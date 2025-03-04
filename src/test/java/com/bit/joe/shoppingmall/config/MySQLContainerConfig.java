package com.bit.joe.shoppingmall.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.testcontainers.containers.MySQLContainer;

public class MySQLContainerConfig
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @SuppressWarnings("resource")
    private static final MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:lts")
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    static {
        mysql.start();
        Runtime.getRuntime().addShutdownHook(new Thread(mysql::stop));
    }

    public static MySQLContainer<?> getInstance() {
        return mysql;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:lts");
        mySQLContainer.start();

        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        Map<String, Object> testContainers = new HashMap<>();
        testContainers.put("spring.datasource.url", mySQLContainer.getJdbcUrl());
        testContainers.put("spring.datasource.username", mySQLContainer.getUsername());
        testContainers.put("spring.datasource.password", mySQLContainer.getPassword());

        environment
                .getPropertySources()
                .addFirst(new MapPropertySource("testcontainers", testContainers));
    }
}
