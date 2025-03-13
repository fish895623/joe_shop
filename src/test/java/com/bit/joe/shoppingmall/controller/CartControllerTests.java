package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.bit.joe.shoppingmall.dto.request.CartRequest;
import com.bit.joe.shoppingmall.entity.Category;
import com.bit.joe.shoppingmall.entity.Product;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.jwt.JWTUtil;
import com.bit.joe.shoppingmall.mapper.UserMapper;
import com.bit.joe.shoppingmall.repository.*;
import com.bit.joe.shoppingmall.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
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
                    .email("admin Cart")
                    .password("admin")
                    .build();
    User userEntity =
            User.builder()
                    .name("user")
                    .password("user")
                    .email("user Cart")
                    .role(UserRole.USER)
                    .gender(UserGender.MALE)
                    .birth("2021-01-01")
                    .build();

    String adminJwtToken;
    String userJwtToken;
    User user;
    User admin;
    Product product;
    Category category;

    @Autowired private MockMvc mockMvc;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserService userService;
    @Autowired private JWTUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        // Create user
        userService.createUser(UserMapper.toDto(adminEntity));
        userService.createUser(UserMapper.toDto(userEntity));

        user = UserMapper.toEntity(userService.getUserByEmail(userEntity.getEmail()).getUser());
        admin = UserMapper.toEntity(userService.getUserByEmail(adminEntity.getEmail()).getUser());

        adminJwtToken =
                jwtUtil.createJwt(
                        adminEntity.getEmail(), adminEntity.getRole().toString(), 36000000L);
        userJwtToken =
                jwtUtil.createJwt(
                        userEntity.getEmail(), userEntity.getRole().toString(), 36000000L);

        // Prepare data
        Category categoryEntity =
                Category.builder().categoryName("Test Category for Cart Test").build();
        category = categoryRepository.save(categoryEntity);

        Product productEntity =
                Product.builder()
                        .name("Test Product for Cart Test")
                        .category(category)
                        .imageURL("image")
                        .quantity(10)
                        .price(1000)
                        .build();
        product = productRepository.save(productEntity);

        categoryRepository.flush();
        productRepository.flush();
    }

    @Test
    void adminCreateCart() throws Exception {

        // make cookie
        var cookie = new Cookie("token", adminJwtToken);
        cookie.setPath("/");

        // prepare data
        CartRequest cartRequest = CartRequest.builder().userId(admin.getId()).build();
        var insertData = new ObjectMapper().writeValueAsString(cartRequest);

        // perform login
        mockMvc.perform(
                        post("/api/cart/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(cookie)
                                .content(insertData))
                .andExpect(status().isOk());

        // perform get cart
        mockMvc.perform(get("/api/cart/get").contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andExpect(status().isOk());
    }

    @Test
    void userCreateCart() throws Exception {

        // make cookie
        var cookie = new Cookie("token", userJwtToken);
        cookie.setPath("/");

        // prepare data
        CartRequest cartRequest = CartRequest.builder().userId(user.getId()).build();
        var insertData = new ObjectMapper().writeValueAsString(cartRequest);

        // perform login
        mockMvc.perform(
                        post("/api/cart/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(cookie)
                                .content(insertData))
                .andExpect(status().isOk());

        // perform get cart
        mockMvc.perform(get("/api/cart/get").contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get cart with invalid token")
    void getCartWithInvalidToken() throws Exception {
        // make cookie
        var cookie = new Cookie("token", userJwtToken);
        cookie.setPath("/");

        // perform get cart
        mockMvc.perform(get("/api/cart/get").contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Create cart with invalid request(user id)")
    void createCartWithInvalidRequest() throws Exception {
        // prepare data
        CartRequest cartRequest = CartRequest.builder().userId(0L).build();
        var insertData = new ObjectMapper().writeValueAsString(cartRequest);

        // perform login
        mockMvc.perform(
                        post("/api/cart/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Append product to cart TEST")
    public void appendProductToCartTest() throws Exception {
        // Create Cart ========================================================================

        // Prepare data
        var cookie = new Cookie("token", userJwtToken);
        cookie.setPath("/");

        CartRequest cartRequest = CartRequest.builder().userId(user.getId()).build();
        var insertData = new ObjectMapper().writeValueAsString(cartRequest);

        mockMvc.perform(
                        post("/api/cart/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData)
                                .cookie(cookie)) // for Authorization
                .andExpect(status().isOk());

        // Append product to cart =============================================================

        // Prepare data
        cartRequest = CartRequest.builder().productId(product.getId()).quantity(3).build();
        insertData = new ObjectMapper().writeValueAsString(cartRequest);

        mockMvc.perform(
                        post("/api/cart/append")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData)
                            .cookie(cookie)) // for Authorization
                .andExpect(status().isOk());

        mockMvc.perform(
                post("/api/cart/append")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(insertData)
                    .cookie(cookie)) // for Authorization
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Remove product from cart TEST")
    public void removeProductFromCartTest() throws Exception {
        appendProductToCartTest();

        // prepare data
        CartRequest cartRequest = CartRequest.builder().productId(product.getId()).quantity(3).build();
        var insertData = new ObjectMapper().writeValueAsString(cartRequest);

        mockMvc.perform(
                        post("/api/cart/remove")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData)
                                .cookie(new Cookie("token", userJwtToken)))
                .andExpect(status().isOk());

        cartRequest = CartRequest.builder().productId(product.getId()).quantity(4).build();
        insertData = new ObjectMapper().writeValueAsString(cartRequest);

        mockMvc.perform(
                post("/api/cart/remove")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(insertData)
                    .cookie(new Cookie("token", userJwtToken)))
            .andExpect(status().isOk());

        // prepare data
        cartRequest = CartRequest.builder().productId(0L).quantity(3).build();
        insertData = new ObjectMapper().writeValueAsString(cartRequest);

        mockMvc.perform(
                post("/api/cart/remove")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(insertData)
                    .cookie(new Cookie("token", userJwtToken)))
            .andExpect(status().isOk());
    }
}
