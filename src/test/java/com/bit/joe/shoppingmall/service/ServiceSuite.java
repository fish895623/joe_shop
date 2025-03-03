package com.bit.joe.shoppingmall.service;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    CategoryServiceTests.class,
    CartServiceTests.class,
    CategoryServiceTests.class,
    OrderServiceTests.class,
    ProductServiceTests.class,
    UserServiceTests.class,
})
public class ServiceSuite {}
