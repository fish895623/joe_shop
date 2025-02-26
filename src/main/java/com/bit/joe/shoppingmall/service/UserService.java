package com.bit.joe.shoppingmall.service;

import com.bit.joe.shoppingmall.dto.Response;
import com.bit.joe.shoppingmall.dto.UserDto;

public interface UserService {
    Response createUser(UserDto userRequest);

    Response updateUser(Long userId, UserDto userRequest);

    Response getAllUsers();

    Response getUserById(Long userId);

    Response getUserByEmail(String email);

    Response deleteUser(Long userId);

    Response login(String email, String password);

    Response logout(String email) throws Exception;
}
