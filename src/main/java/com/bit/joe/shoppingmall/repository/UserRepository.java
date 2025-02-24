package com.bit.joe.shoppingmall.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bit.joe.shoppingmall.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
