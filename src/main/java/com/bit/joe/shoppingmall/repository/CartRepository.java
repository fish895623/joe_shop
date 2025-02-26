package com.bit.joe.shoppingmall.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bit.joe.shoppingmall.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {}
