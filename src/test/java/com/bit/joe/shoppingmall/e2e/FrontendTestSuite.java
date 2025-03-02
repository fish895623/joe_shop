package com.bit.joe.shoppingmall.e2e;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.bit.joe.shoppingmall.ContainerTests;

@Suite
@SelectClasses({
    ContainerTests.class,
    ExampleTests.class,
})
public class FrontendTestSuite {}
