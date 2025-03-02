package com.bit.joe.shoppingmall.service;

import org.junit.jupiter.api.*;
import org.mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartServiceTests {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
}
