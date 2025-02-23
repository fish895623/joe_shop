package com.bit.joe.shoppingmall.repository;

import com.bit.joe.shoppingmall.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
