package com.bit.joe.shoppingmall.repository;

import com.bit.joe.shoppingmall.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {}
