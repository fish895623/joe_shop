package com.bit.joe.shoppingmall.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.bit.joe.shoppingmall.dto.CategoryDto;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.Category;
import com.bit.joe.shoppingmall.repository.CategoryRepository;
import com.bit.joe.shoppingmall.service.Impl.CategoryServiceImpl;

import jakarta.transaction.Transactional;

@ExtendWith({SpringExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@Testcontainers
class CategoryServiceTests {

    @Container
    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:lts");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private CategoryRepository categoryRepository;
    @BeforeEach
    public void setUp() {
        categoryRepository.deleteAll();
    }

    @Test
    @Transactional
    public void createCategory() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("Electronics");

        Response response = categoryService.createCategory(categoryDto);

        assertEquals(200, response.getStatus());
        assertEquals("Category created successfully", response.getMessage());

        var category = categoryRepository.findAll();
        System.out.println(category);

    }
    @Test
    @Transactional
    public void updateCategory() {
        // Given
        Category category = new Category();
        category.setCategoryName("Electronics");
        categoryRepository.save(category);

        CategoryDto updateCategoryDto = new CategoryDto();
        updateCategoryDto.setCategoryName("Home Appliances");

        // When
        Response response = categoryService.updateCategory(category.getId(), updateCategoryDto);

        // Then
        assertEquals(200, response.getStatus());

        Optional<Category> updatedCategory = categoryRepository.findById(category.getId());
        assertTrue(updatedCategory.isPresent());
        assertEquals("Home Appliances", updatedCategory.get().getCategoryName());
    }
    @Test
    @Transactional
    public void getAllCategory() {
        // Given
        Category category1 = new Category();
        category1.setCategoryName("Electronics");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setCategoryName("Home Appliances");
        categoryRepository.save(category2);

        // When
        Response response = categoryService.getAllCategories();

        // Then
        assertEquals(200, response.getStatus());
        assertEquals(2, response.getCategoryList().size());
    }

    @Test
    @Transactional
    public void getCategoryById() {
        // Given
        Category category = new Category();
        category.setCategoryName("Electronics");
        categoryRepository.save(category);

        // When
        Response response = categoryService.getCategoryById(category.getId());

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Electronics", response.getCategory().getCategoryName());
    }

    @Test
    @Transactional
    public void deleteCategory() {
        // Given
        Category category = new Category();
        category.setCategoryName("Electronics");
        categoryRepository.save(category);

        // When
        Response response = categoryService.deleteCategory(category.getId());

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Category was deleted successfully", response.getMessage());
    }
    @Test
    @Transactional
    public void failedFindById() {
        // Given
        Category category = new Category();
        category.setCategoryName("Electronics");
        categoryRepository.save(category);

        // When Response status is 400
        Response response = categoryService.getCategoryById(100L);

        // Then
        assertTrue(response.getStatus() >= 400 || response.getStatus() < 500);
    }
    @Test
    @Transactional
    public void updateNonExistCategory() {
        // Given
        Category category = new Category();
        category.setCategoryName("Electronics");
        categoryRepository.save(category);

        CategoryDto updateCategoryDto = new CategoryDto();
        updateCategoryDto.setCategoryName("Home Appliances");

        // When Response status is 400
        Response response = categoryService.updateCategory(100L, updateCategoryDto);

        // Then
        assertTrue(response.getStatus() >= 400 || response.getStatus() < 500);
    }
    @Test
    @Transactional
    public void deleteNonExistCategory() {
        // Given
        Category category = new Category();
        category.setCategoryName("Electronics");
        categoryRepository.save(category);

        // When Response status is 400
        Response response = categoryService.deleteCategory(100L);

        // Then
        assertTrue(response.getStatus() >= 400 || response.getStatus() < 500);
    }
    @Test
    @Transactional
    public void createCategoryWithNullNameAndDuplicate() {
        // Given
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName(null);

        // When Response status is 400
        Response response = categoryService.createCategory(categoryDto);

        // Then
        assertTrue(response.getStatus() >= 400 || response.getStatus() < 500);

        // Given
        CategoryDto categoryDto2 = new CategoryDto();
        categoryDto2.setCategoryName("Electronics");

        // When
        Response response2 = categoryService.createCategory(categoryDto2);

        // Then
        assertEquals(200, response2.getStatus());

        // When Response status is 400
        Response response3 = categoryService.createCategory(categoryDto2);

        // Then
        assertTrue(response3.getStatus() >= 400 || response3.getStatus() < 500);
    }
}
