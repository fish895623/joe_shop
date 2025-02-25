package com.bit.joe.shoppingmall.service.Impl;

import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.exception.NotFoundException;
import com.bit.joe.shoppingmall.mapper.UserMapper;
import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.Response;
import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.repository.UserRepository;
import com.bit.joe.shoppingmall.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Response createUser(UserDto userRequest) throws Exception {

        User user = UserMapper.toEntity(userRequest);
        // Convert UserDto to User
        userRepository.save(user);
        // Save user
        return Response.builder().status(200).message("User created successfully").build();
        // return success response
    }

    @Override
    public Response updateUser(Long userId, UserDto userRequest) {

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User data not found"));
        // Check if user exists
        User user = UserMapper.toEntity(userRequest);
        // Convert UserDto to User
        user.setId(userId);
        // Set target user's id
        userRepository.save(user);
        // Save user
        return Response.builder().status(200).message("User updated successfully").build();
        // return success response
    }

    @Override
    public Response deleteUser(Long userId) {

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User data not fount"));
        // Check if user exists
        userRepository.deleteById(userId);
        // Delete user
        return Response.builder().status(200).message("User deleted successfully").build();
        // return success response
    }

    @Override
    public Response getUserById(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User data not found"));
        // Check if user exists and get user
        UserDto userDto = UserMapper.toDto(user);
        // Convert User to UserDto
        return Response.builder().status(200).message("User data retrieved successfully").user(userDto).build();
        // return success response
    }

    @Override
    public Response getUserByEmail(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User data not found"));
        // Check if user exists and get user
        UserDto userDto = UserMapper.toDto(user);
        // Convert User to UserDto
        return Response.builder().status(200).message("User data retrieved successfully").user(userDto).build();
        // return success response
    }

    @Override
    public Response getAllUsers() throws Exception {

        List<UserDto> userList = userRepository.findAll().stream().map(UserMapper::toDto).toList();
        // Get all users and convert them to UserDto
        return Response.builder().status(200).message("All users retrieved successfully").userList(userList).build();
        // reuturn success response
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
