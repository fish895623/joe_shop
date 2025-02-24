package com.bit.joe.shoppingmall.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bit.joe.shoppingmall.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}
