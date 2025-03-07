package com.bit.joe.shoppingmall.service;

import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.User;

import jakarta.servlet.http.HttpSession;

public interface UserService {
    Response createUser(UserDto userRequest);

    Response updateUser(Long userId, UserDto userRequest);

    Response deleteUser(Long userId);

    Response getUserById(Long userId);

    Response getUserByEmail(String email);

    Response login(String email, String password);

    Response logout(HttpSession session);

    Response withdraw(HttpSession session, UserDto userDto);

    User getLoginUser();

    Response getAllUsers();
}
