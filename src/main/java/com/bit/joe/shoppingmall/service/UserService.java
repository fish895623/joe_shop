package com.bit.joe.shoppingmall.service;

import com.bit.joe.shoppingmall.dto.Response;
import com.bit.joe.shoppingmall.dto.UserDto;

public interface UserService {
    Response createUser(UserDto userRequest) throws Exception;

    Response updateUser(Long userId, UserDto userRequest) throws Exception;

    Response getAllUsers() throws Exception;

    Response getUserById(Long userId) throws Exception;

    Response getUserByEmail(String email) throws Exception;

    Response deleteUser(Long userId) throws Exception;

    Response login(String email, String password) throws Exception;

    Response logout(String email) throws Exception;
}
