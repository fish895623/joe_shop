package com.bit.joe.shoppingmall.repository;

import com.bit.joe.shoppingmall.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
