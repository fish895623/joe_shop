package com.bit.joe.shoppingmall.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bit.joe.shoppingmall.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUserId(Long id);

    Optional<Order> findByOrderDate(LocalDateTime orderDate);

    Optional<Order> findByOrderDateAndUserId(LocalDateTime orderDate, Long userId);
}
