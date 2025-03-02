package com.bit.joe.shoppingmall.controller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.MySQLContainer;

import com.bit.joe.shoppingmall.config.MySQLContainerConfig;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerTests {
    static final MySQLContainer<?> mysql = MySQLContainerConfig.getInstance();

    @Test
    @Order(1)
    void test1() {
        System.out.println("test1");
    }
}
