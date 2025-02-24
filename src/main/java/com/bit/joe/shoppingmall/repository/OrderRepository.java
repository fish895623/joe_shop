package com.bit.joe.shoppingmall.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bit.joe.shoppingmall.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {}
