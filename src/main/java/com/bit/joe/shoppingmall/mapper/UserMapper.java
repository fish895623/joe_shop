package com.bit.joe.shoppingmall.mapper;

import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.entity.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setGender(user.getGender());
        userDto.setRole(user.getRole());
        userDto.setBirth(user.getBirth());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        return userDto;
    }

    public static User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setGender(userDto.getGender());
        user.setRole(userDto.getRole());
        user.setBirth(userDto.getBirth());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        return user;
    }
}