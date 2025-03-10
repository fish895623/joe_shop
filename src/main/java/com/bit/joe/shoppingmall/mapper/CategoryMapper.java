package com.bit.joe.shoppingmall.mapper;

import org.springframework.stereotype.Component;

import com.bit.joe.shoppingmall.dto.CategoryDto;
import com.bit.joe.shoppingmall.entity.Category;

@Component
public class CategoryMapper {

    public static CategoryDto categoryToDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setCategoryName(category.getCategoryName());
        return categoryDto;
    }

    public static Category toEntity(CategoryDto categoryDto) {
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setCategoryName(categoryDto.getCategoryName());
        return category;
    }
}
