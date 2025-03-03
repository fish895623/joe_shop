package com.bit.joe.shoppingmall.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.bit.joe.shoppingmall.dto.CategoryDto;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.repository.CategoryRepository;
import com.bit.joe.shoppingmall.repository.ProductRepository;
import com.bit.joe.shoppingmall.service.Impl.CategoryServiceImpl;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers(parallel = true)
@ExtendWith(MockitoExtension.class)
class CategoryServiceTests {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @InjectMocks private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Order(1)
    void testCreateCategory() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("test");
        Response response = categoryService.createCategory(categoryDto);

        Assertions.assertEquals(200, response.getStatus());

        Assertions.assertEquals("test", categoryRepository.findById(1L).get().getCategoryName());
    }

    @Test
    @Order(2)
    void testUpdateCategory() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("test2");
        Response response = categoryService.updateCategory(1L, categoryDto);

        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @Order(3)
    void testUpdateNotExistCategory() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("test");
        categoryService.updateCategory(2L, categoryDto);
    }

    @Test
    @Order(4)
    void testGetAllCategories() {
        categoryService.getAllCategories();
    }
}
