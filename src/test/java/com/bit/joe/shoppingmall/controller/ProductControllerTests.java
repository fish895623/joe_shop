package com.bit.joe.shoppingmall.controller;

import org.testcontainers.containers.MySQLContainer;

import com.bit.joe.shoppingmall.config.MySQLContainerConfig;

public class ProductControllerTests {
    static final MySQLContainer<?> mysql = MySQLContainerConfig.getInstance();
}
