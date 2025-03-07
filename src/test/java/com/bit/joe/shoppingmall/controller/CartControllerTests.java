package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.bit.joe.shoppingmall.entity.Category;
import com.bit.joe.shoppingmall.entity.Product;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.mapper.UserMapper;
import com.bit.joe.shoppingmall.repository.*;
import com.bit.joe.shoppingmall.service.UserService;

import jakarta.transaction.Transactional;

@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
@Rollback
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
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserService userService;

    @BeforeEach
    public void setUp() {
        // Create user
        userService.createUser(UserMapper.toDto(adminEntity));
        userService.createUser(UserMapper.toDto(userEntity));

        // Prepare data
        Category category = Category.builder().categoryName("Test Category").build();
        category = categoryRepository.save(category);

        Product product =
                Product.builder()
                        .name("Test Product")
                        .category(category)
                        .imageURL("image")
                        .quantity(10)
                        .price(1000)
                        .build();
        productRepository.save(product);

        categoryRepository.flush();
        productRepository.flush();
    }

    @Test
    void adminCreateCart() throws Exception {
        // perform login
        mockMvc.perform(
                        post("/api/cart/create")
                                .header("Authorization", adminBasicAuth)
                                .content("")
                                .session(mockHttpSession))
                .andExpect(status().isOk());
    }

    @Test
    void userCreateCart() throws Exception {
        // perform login
        mockMvc.perform(
                        post("/api/cart/create")
                                .header("Authorization", userBasicAuth)
                                .content("")
                                .session(mockHttpSession))
                .andExpect(status().isOk());
    }
}
