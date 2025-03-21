package com.bit.joe.shoppingmall.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.CategoryDto;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.Category;
import com.bit.joe.shoppingmall.exception.NotFoundException;
import com.bit.joe.shoppingmall.mapper.CategoryMapper;
import com.bit.joe.shoppingmall.repository.CategoryRepository;
import com.bit.joe.shoppingmall.service.CategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Response createCategory(CategoryDto categoryRequest) {
        Category category = new Category();
        category.setCategoryName(categoryRequest.getCategoryName());
        category = categoryRepository.save(category);
        return Response.builder()
                .status(200)
                .message("Category created successfully")
                .category(CategoryMapper.categoryToDto(category))
                .build();
    }

    @Override
    public Response updateCategory(Long categoryId, CategoryDto categoryRequest) {
        Category category =
                categoryRepository
                        .findById(categoryId)
                        .orElseThrow(() -> new NotFoundException("Category Not Found"));
        category.setCategoryName(categoryRequest.getCategoryName());
        categoryRepository.save(category);
        return Response.builder().status(200).message("category updated successfully").build();
    }

    @Override
    public Response getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> categoryDtoList =
                categories.stream().map(CategoryMapper::categoryToDto).collect(Collectors.toList());

        return Response.builder().status(200).categoryList(categoryDtoList).build();
    }

    @Override
    public Response getCategoryById(Long categoryId) {
        Category category =
                categoryRepository
                        .findById(categoryId)
                        .orElseThrow(() -> new NotFoundException("Category Not Found"));
        CategoryDto categoryDto = CategoryMapper.categoryToDto(category);

        return Response.builder().status(200).category(categoryDto).build();
    }

    @Override
    public Response getCategoryByName(CategoryDto categoryNameDto) {
        String categoryName = categoryNameDto.getCategoryName();
        Category category =
                categoryRepository
                        .findByCategoryName(categoryName)
                        .orElseThrow(() -> new NotFoundException("Category Not Found"));
        CategoryDto categoryDto = CategoryMapper.categoryToDto(category);

        return Response.builder().status(200).category(categoryDto).build();
    }

    @Override
    public Response deleteCategory(Long categoryId) {
        Category category =
                categoryRepository
                        .findById(categoryId)
                        .orElseThrow(() -> new NotFoundException("Category Not Found"));
        categoryRepository.delete(category);
        return Response.builder().status(200).message("Category was deleted successfully").build();
    }
}
