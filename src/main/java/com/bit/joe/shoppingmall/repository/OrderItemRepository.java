package com.bit.joe.shoppingmall.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bit.joe.shoppingmall.entity.Order;
import com.bit.joe.shoppingmall.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findByOrder(Order order);
}
