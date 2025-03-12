package com.bit.joe.shoppingmall.service;

import com.bit.joe.shoppingmall.dto.CategoryDto;
import com.bit.joe.shoppingmall.dto.response.Response;

public interface CategoryService {
    Response createCategory(CategoryDto categoryRequest);

    Response updateCategory(Long categoryId, CategoryDto categoryRequest);

    Response getAllCategories();

    Response getCategoryById(Long categoryId);

    Response getCategoryByName(CategoryDto categoryDto);

    Response deleteCategory(Long categoryId);
}
