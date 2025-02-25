package com.bit.joe.shoppingmall.service.Impl;

import com.bit.joe.shoppingmall.dto.Response;
import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


  @Override
  public Response createUser(UserDto userRequest) {
    return null;
  }

  @Override
  public Response updateUser(Long userId, UserDto userRequest) {
    return null;
  }

  @Override
  public Response getAllUsers() {
    return null;
  }

  @Override
  public Response getUserById(Long userId) {
    return null;
  }

  @Override
  public Response getUserByEmail(String email) {
    return null;
  }

  @Override
  public Response deleteUser(Long userId) {
    return null;
  }

  @Override
  public Response login(String email, String password) {
    return null;
  }

  @Override
  public Response logout(String email) {
    return null;
  }
}
