package com.bit.joe.shoppingmall.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bit.joe.shoppingmall.entity.Order;
import com.bit.joe.shoppingmall.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    Optional<Order> findByOrderDate(LocalDateTime orderDate);

    Optional<Order> findByOrderDateAndUserId(LocalDateTime orderDate, Long userId);
}
