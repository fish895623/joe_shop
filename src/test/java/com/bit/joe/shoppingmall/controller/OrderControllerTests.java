package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;
import java.util.List;

import com.bit.joe.shoppingmall.dto.request.CartRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.bit.joe.shoppingmall.dto.*;
import com.bit.joe.shoppingmall.dto.request.OrderRequest;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.repository.CartRepository;
import com.bit.joe.shoppingmall.repository.CategoryRepository;
import com.bit.joe.shoppingmall.repository.ProductRepository;
import com.bit.joe.shoppingmall.service.Impl.CartService;
import com.bit.joe.shoppingmall.service.Impl.CategoryServiceImpl;
import com.bit.joe.shoppingmall.service.Impl.ProductServiceImpl;
import com.bit.joe.shoppingmall.service.UserService;

import jakarta.transaction.Transactional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
public class OrderControllerTests {
    UserDto userDto =
            UserDto.builder()
                    .name("user")
                    .password("user")
                    .email("user@example.com")
                    .role(UserRole.USER)
                    .gender(UserGender.MALE)
                    .birth("2021-01-01")
                    .build();

    String userBasicAuth =
            "Basic "
                    + Base64.getEncoder()
                            .encodeToString(
                                    (userDto.getEmail() + ":" + userDto.getPassword()).getBytes());

    @Autowired private MockMvc mockMvc;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CategoryServiceImpl categoryService;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductServiceImpl productService;
    @Autowired private CartRepository cartRepository;
    @Autowired private UserService userService;
    @Autowired private CartService cartService;

    /**
     * Set up before each test
     */
    @BeforeEach
    void setUp() {
        userService.createUser(userDto);

        var categoryDto1 = CategoryDto.builder().id(1L).categoryName("category1").build();
        var categoryDto2 = CategoryDto.builder().id(2L).categoryName("category2").build();
        var categoryDto3 = CategoryDto.builder().id(3L).categoryName("category3").build();
        categoryService.createCategory(categoryDto1);
        categoryService.createCategory(categoryDto2);
        categoryService.createCategory(categoryDto3);

        categoryRepository.flush();

        var productDto1 =
                ProductDto.builder()
                        .id(1L)
                        .name("product1")
                        .price(100)
                        .quantity(15)
                        .imageUrl("image1")
                        .category(categoryDto1)
                        .build();
        var productDto2 =
                ProductDto.builder()
                        .id(2L)
                        .name("product2")
                        .price(200)
                        .quantity(10)
                        .imageUrl("image2")
                        .category(categoryDto2)
                        .build();
        var productDto3 =
                ProductDto.builder()
                        .id(3L)
                        .name("product3")
                        .price(300)
                        .quantity(5)
                        .imageUrl("image3")
                        .category(categoryDto3)
                        .build();

        productService.createProduct(
                productDto1.getCategory().getId(),
                productDto1.getImageUrl(),
                productDto1.getName(),
                productDto1.getQuantity(),
                productDto1.getPrice());
        productService.createProduct(
                productDto2.getCategory().getId(),
                productDto2.getImageUrl(),
                productDto2.getName(),
                productDto2.getQuantity(),
                productDto2.getPrice());
        productService.createProduct(
                productDto3.getCategory().getId(),
                productDto3.getImageUrl(),
                productDto3.getName(),
                productDto3.getQuantity(),
                productDto3.getPrice());

        productRepository.flush();

        // Set authentication in the context holder
        var auth =
                new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        cartService.createCart();

        var cartRequest1 = CartRequest.builder().productId(1L).quantity(1).build();
        var cartRequest2 = CartRequest.builder().productId(2L).quantity(2).build();
        var cartRequest3 = CartRequest.builder().productId(3L).quantity(3).build();

        cartService.appendProductToCart(cartRequest1);
        cartService.appendProductToCart(cartRequest2);
        cartService.appendProductToCart(cartRequest3);

        cartRepository.flush();
    }

    /**
     * Test create order
     *
     * @throws Exception
     */
    @Test
    void createOrder() throws Exception {
        // prepare request data
        List<Long> cartItemIds = List.of(1L, 2L, 3L);

        // create order request
        OrderRequest orderRequest =
                OrderRequest.builder()
                        .userId(1L)
                        .cartItemIds(cartItemIds)
                        .build();

        // convert to json
        var insertData = new ObjectMapper().writeValueAsString(orderRequest);

        // perform request
        mockMvc.perform(
                        post("/api/order/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData)
                                .header("Authorization", userBasicAuth))
                .andExpect(status().isOk());
    }
}
