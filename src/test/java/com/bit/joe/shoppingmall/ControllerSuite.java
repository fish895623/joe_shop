package com.bit.joe.shoppingmall;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.bit.joe.shoppingmall.controller.CategoryControllerTests;
import com.bit.joe.shoppingmall.controller.ProductControllerTests;
import com.bit.joe.shoppingmall.controller.UserControllerTests;

@Suite
@SelectClasses({
    ContainerTests.class,
    UserControllerTests.class,
    CategoryControllerTests.class,
    ProductControllerTests.class,
})
public class ControllerSuite {}
