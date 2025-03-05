package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.bit.joe.shoppingmall.entity.Category;
import com.bit.joe.shoppingmall.entity.Product;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.mapper.CategoryMapper;
import com.bit.joe.shoppingmall.mapper.ProductMapper;
import com.bit.joe.shoppingmall.mapper.UserMapper;
import com.bit.joe.shoppingmall.repository.CartRepository;
import com.bit.joe.shoppingmall.repository.CategoryRepository;
import com.bit.joe.shoppingmall.repository.ProductRepository;
import com.bit.joe.shoppingmall.repository.UserRepository;
import com.bit.joe.shoppingmall.service.Impl.CartService;
import com.bit.joe.shoppingmall.service.Impl.CategoryServiceImpl;
import com.bit.joe.shoppingmall.service.Impl.ProductServiceImpl;
import com.bit.joe.shoppingmall.service.UserService;

@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
class CartControllerTests {
    User adminEntity =
            User.builder()
                    .name("admin")
                    .gender(UserGender.MALE)
                    .role(UserRole.ADMIN)
                    .birth("2021-01-01")
                    .email("admin@example.com")
                    .password("admin")
                    .build();
    User userEntity =
            User.builder()
                    .name("user")
                    .password("user")
                    .email("user@example.com")
                    .role(UserRole.USER)
                    .gender(UserGender.MALE)
                    .birth("2021-01-01")
                    .build();
    String adminBasicAuth =
            "Basic "
                    + Base64.getEncoder()
                            .encodeToString(
                                    (adminEntity.getEmail() + ":" + adminEntity.getPassword())
                                            .getBytes());
    String userBasicAuth =
            "Basic "
                    + Base64.getEncoder()
                            .encodeToString(
                                    (userEntity.getEmail() + ":" + userEntity.getPassword())
                                            .getBytes());
    MockHttpSession mockHttpSession = new MockHttpSession();
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CategoryServiceImpl categoryService;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductServiceImpl productService;
    @Autowired private CartController cartController;
    @Autowired private CartService cartService;
    @Autowired private CartRepository cartRepository;
    @Autowired private UserService userService;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();
        cartRepository.deleteAll();

        // Create user
        userService.createUser(UserMapper.toDto(adminEntity));
        userService.createUser(UserMapper.toDto(userEntity));

        // Prepare data
        Category category = Category.builder().id(1L).categoryName("Test Category").build();
        categoryService.createCategory(CategoryMapper.categoryToDto(category));

        Product product =
                Product.builder()
                        .id(1L)
                        .name("Test Product")
                        .category(category)
                        .imageURL("image")
                        .quantity(10)
                        .price(1000)
                        .build();
        var productDto = ProductMapper.toDto(product);

        productService.createProduct(
                productDto.getCategory().getId(),
                productDto.getImageUrl(),
                productDto.getName(),
                productDto.getQuantity(),
                productDto.getPrice());
    }

    @Test
    void createCart() throws Exception {
        // perform login
        mockMvc.perform(
                        post("/api/cart/create")
                                .header("Authorization", adminBasicAuth)
                                .content("")
                                .session(mockHttpSession))
                .andExpect(status().isOk());
    }

    @Test
    void appendProductToCart() {}

    @Test
    void removeProductFromCart() {}
}
