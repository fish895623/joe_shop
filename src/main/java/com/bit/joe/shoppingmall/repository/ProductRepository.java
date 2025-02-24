package com.bit.joe.shoppingmall.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bit.joe.shoppingmall.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
}
