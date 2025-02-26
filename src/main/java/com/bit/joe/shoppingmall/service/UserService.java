package com.bit.joe.shoppingmall.service;

import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.response.Response;

public interface UserService {
    Response createUser(UserDto userRequest);

    Response updateUser(Long userId, UserDto userRequest);

    Response getAllUsers();

    Response getUserById(Long userId);

    Response getUserByEmail(String email);

    Response deleteUser(Long userId);

    Response login(String email, String password) throws Exception;

    Response logout(String email) throws Exception;
}
