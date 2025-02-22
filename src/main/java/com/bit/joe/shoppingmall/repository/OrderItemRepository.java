package com.bit.joe.shoppingmall.repository;

import com.bit.joe.shoppingmall.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
