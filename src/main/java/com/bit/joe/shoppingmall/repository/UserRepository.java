package com.bit.joe.shoppingmall.repository;

import com.bit.joe.shoppingmall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
