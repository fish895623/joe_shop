package com.bit.joe.shoppingmall.service;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    UserServiceTest.class,
})
public class ServiceSuite {}
