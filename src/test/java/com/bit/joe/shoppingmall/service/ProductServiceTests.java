package com.bit.joe.shoppingmall.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bit.joe.shoppingmall.repository.CategoryRepository;
import com.bit.joe.shoppingmall.repository.ProductRepository;
import com.bit.joe.shoppingmall.service.Impl.ProductServiceImpl;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @InjectMocks private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Order(1)
    void testCreateProduct() {
        productService.createProduct(1L, "image", "name", 1, 1);
    }
}
