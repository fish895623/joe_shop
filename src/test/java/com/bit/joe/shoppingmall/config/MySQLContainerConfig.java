package com.bit.joe.shoppingmall.config;

import org.testcontainers.containers.MySQLContainer;

public class MySQLContainerConfig {

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
}
