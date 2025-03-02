package com.bit.joe.shoppingmall;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.bit.joe.shoppingmall.controller.ControllerSuite;
import com.bit.joe.shoppingmall.e2e.FrontendTestSuite;
import com.bit.joe.shoppingmall.service.ServiceSuite;

@Suite
@SelectClasses({
    ContainerTests.class,
    ServiceSuite.class,
    ControllerSuite.class,
    FrontendTestSuite.class,
})
public class TestRunner {}
