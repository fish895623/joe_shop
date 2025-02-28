package com.bit.joe.shoppingmall.service.Impl;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.exception.NotFoundException;
import com.bit.joe.shoppingmall.mapper.UserMapper;
import com.bit.joe.shoppingmall.repository.UserRepository;
import com.bit.joe.shoppingmall.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Response createUser(UserDto userRequest) {
        User user = UserMapper.toEntity(userRequest);
        // Convert UserDto to User
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        // Encode password

        userRepository.save(user);
        // Save user
        return Response.builder().status(200).message("User created successfully").build();
    }

    @Override
    public Response updateUser(Long userId, UserDto userRequest) {

        userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new NotFoundException("User data going to update does not found"));
        // Check if user exists and throw exception if not

        User user = UserMapper.toEntity(userRequest);
        // Convert UserDto to User
        user.setId(userId);
        // Set target user's id
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        // Encode password

        userRepository.save(user);
        // Save user

        return Response.builder().status(200).message("User updated successfully").build();
        // return success response
    }

    @Override
    public Response deleteUser(Long userId) {

        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User data not fount"));
        // Check if user exists
        userRepository.deleteById(userId);
        // Delete user
        return Response.builder().status(200).message("User deleted successfully").build();
        // return success response
    }

    @Override
    public Response getUserById(Long userId) {

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new NotFoundException("User data not found"));
        // Check if user exists and get user
        UserDto userDto = UserMapper.toDto(user);
        // Convert User to UserDto
        return Response.builder()
                .status(200)
                .message("User data retrieved successfully")
                .user(userDto)
                .build();
        // return success response
    }

    @Override
    public Response getUserByEmail(String email) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new NotFoundException("User data not found"));
        // Check if user exists and get user
        UserDto userDto = UserMapper.toDto(user);
        // Convert User to UserDto
        return Response.builder()
                .status(200)
                .message("User data retrieved successfully")
                .user(userDto)
                .build();
        // return success response
    }

    // Get all users -> just for testing
    @Override
    public Response getAllUsers() {

        List<UserDto> userList = userRepository.findAll().stream().map(UserMapper::toDto).toList();
        // Get all users and convert them to UserDto
        return Response.builder()
                .status(200)
                .message("All users retrieved successfully")
                .userList(userList)
                .build();
        // reuturn success response
    }

    @Override
    public Response login(String email, String password) {

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new NotFoundException("User data not found"));
        // Get user by email , check if user exists

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return Response.builder().status(401).message("Wrong Password").build();
        }
        // Check if password is correct (use BCryptPasswordEncoder)

        return Response.builder()
                .status(200)
                .message("Login successful")
                .user(UserMapper.toDto(user))
                .build();
        // Return success response if login is successful
    }

    @Override
    public Response logout(HttpSession session) {

        session.invalidate();
        // Invalidate session -> logout user

        // Clear the SecurityContextHolderStrategy

        // Clear teh SecurityContextRepository

        // Clean up any RememberMe authentication

        // Clear out any saved CSRF token

        // Fire a LogoutSuccessEvent

        return Response.builder().status(200).message("Logout successfully").build();
    }
}
