package com.bit.joe.shoppingmall.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bit.joe.shoppingmall.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    boolean existsByCategoryIdAndName(Long categoryId, String name);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
