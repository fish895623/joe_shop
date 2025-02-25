package com.bit.joe.shoppingmall.service.Impl;

import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.Response;
import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.repository.UserRepository;
import com.bit.joe.shoppingmall.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Response createUser(UserDto userRequest) throws Exception {
        throw new Exception("Not implemented");
    }

    @Override
    public Response updateUser(Long userId, UserDto userRequest) throws Exception {
        throw new Exception("Not implemented");
    }

    @Override
    public Response getAllUsers() throws Exception {
        throw new Exception("Not implemented");
    }

    @Override
    public Response getUserById(Long userId) throws Exception {
        throw new Exception("Not implemented");
    }

    @Override
    public Response getUserByEmail(String email) throws Exception {
        throw new Exception("Not implemented");
    }

    @Override
    public Response deleteUser(Long userId) throws Exception {
        throw new Exception("Not implemented");
    }

    @Override
    public Response login(String email, String password) throws Exception {
        throw new Exception("Not implemented");
    }

    @Override
    public Response logout(String email) throws Exception {
        throw new Exception("Not implemented");
    }
}
