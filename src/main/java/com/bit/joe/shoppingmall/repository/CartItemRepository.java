package com.bit.joe.shoppingmall.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bit.joe.shoppingmall.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {}
