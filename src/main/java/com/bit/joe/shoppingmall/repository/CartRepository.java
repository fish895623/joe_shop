package com.bit.joe.shoppingmall.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bit.joe.shoppingmall.entity.Cart;
import com.bit.joe.shoppingmall.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findCartByUser(User user);
    Cart findByUserId(Long userId);
}
