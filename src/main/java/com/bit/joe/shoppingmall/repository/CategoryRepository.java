package com.bit.joe.shoppingmall.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bit.joe.shoppingmall.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
