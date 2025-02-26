package com.bit.joe.shoppingmall.service;

import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.dto.shopDto.CategoryDto;

public interface CategoryService {
    Response createCategory(CategoryDto categoryRequest);

    Response updateCategory(Long categoryId, CategoryDto categoryRequest);

    Response getAllCategories();

    Response getCategoryById(Long categoryId);

    Response deleteCategory(Long categoryId);
}
