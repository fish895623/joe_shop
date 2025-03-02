package com.bit.joe.shoppingmall.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    @Test
    @Order(1)
    void createUser() {
        Assertions.assertThat(1).isEqualTo(0);
    }

    @Test
    void updateUser() {}

    @Test
    void deleteUser() {}

    @Test
    void getUserById() {}

    @Test
    void getUserByEmail() {}

    @Test
    void getAllUsers() {}

    @Test
    void login() {}

    @Test
    void logout() {}
}
