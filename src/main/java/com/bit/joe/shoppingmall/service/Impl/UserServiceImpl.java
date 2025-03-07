package com.bit.joe.shoppingmall.service.Impl;

import static com.bit.joe.shoppingmall.enums.OrderStatus.*;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true);
        userRepository.save(user);
        return Response.builder().status(200).message("User created successfully").build();
    }

    @Override
    public Response updateUser(Long userId, UserDto userRequest) {
        userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new NotFoundException("User data going to update does not found"));

        User user = UserMapper.toEntity(userRequest);
        user.setId(userId);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        return Response.builder().status(200).message("User updated successfully").build();
    }

    @Override
    public Response deleteUser(Long userId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("User data not found"));
        userRepository.deleteById(userId);
        return Response.builder().status(200).message("User deleted successfully").build();
    }

    @Override
    public Response getUserById(Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new NotFoundException("User data not found"));
        UserDto userDto = UserMapper.toDto(user);
        return Response.builder()
                .status(200)
                .message("User data retrieved successfully")
                .user(userDto)
                .build();
    }

    @Override
    public Response getUserByEmail(String email) {
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new NotFoundException("User data not found"));
        UserDto userDto = UserMapper.toDto(user);
        return Response.builder()
                .status(200)
                .message("User data retrieved successfully")
                .user(userDto)
                .build();
    }

    @Override
    public Response login(String email, String password) {
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new NotFoundException("User data not found"));
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return Response.builder().status(401).message("Wrong Password").build();
        }
        return Response.builder()
                .status(200)
                .message("Login successful")
                .user(UserMapper.toDto(user))
                .build();
    }

    @Override
    public Response logout(HttpSession session) {
        session.invalidate();
        return Response.builder().status(200).message("Logout successfully").build();
    }

    @Override
    public Response withdraw(HttpSession session, UserDto userDto) {
        User user =
                userRepository
                        .findById(userDto.getId())
                        .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getOrders() != null
                && user.getOrders().stream()
                        .anyMatch(order -> !order.getStatus().equals(COMPLETE))) {
            return Response.builder()
                    .status(400)
                    .message("Cannot withdraw account, order progressing")
                    .build();
        }

        user.setActive(false);
        userRepository.save(user);
        session.invalidate();
        return Response.builder().status(200).message("Withdraw successfully").build();
    }

    @Override
    public User getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not found"));
    }

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
}
