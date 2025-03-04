package com.bit.joe.shoppingmall;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.bit.joe.shoppingmall.e2e.FrontendTestSuite;

@Suite
@SelectClasses({
    ContainerTests.class,
    //    ControllerSuite.class,
    FrontendTestSuite.class,
})
public class TestRunner {}
