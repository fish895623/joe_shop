package com.bit.joe.shoppingmall.controller;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    UserControllerTests.class,
    CategoryControllerTests.class,
    ProductControllerTests.class,
})
public class ControllerSuite {}
