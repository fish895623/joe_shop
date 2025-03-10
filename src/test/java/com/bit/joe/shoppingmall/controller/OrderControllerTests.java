package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.bit.joe.shoppingmall.dto.*;
import com.bit.joe.shoppingmall.dto.request.CartRequest;
import com.bit.joe.shoppingmall.dto.request.OrderRequest;
import com.bit.joe.shoppingmall.entity.Cart;
import com.bit.joe.shoppingmall.entity.Category;
import com.bit.joe.shoppingmall.enums.OrderStatus;
import com.bit.joe.shoppingmall.enums.RequestType;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.mapper.CartMapper;
import com.bit.joe.shoppingmall.repository.CartRepository;
import com.bit.joe.shoppingmall.repository.CategoryRepository;
import com.bit.joe.shoppingmall.repository.ProductRepository;
import com.bit.joe.shoppingmall.service.Impl.CartService;
import com.bit.joe.shoppingmall.service.Impl.CategoryServiceImpl;
import com.bit.joe.shoppingmall.service.Impl.ProductServiceImpl;
import com.bit.joe.shoppingmall.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.transaction.Transactional;

/** OrderControllerTests */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
@Rollback
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

    Cart cart;

    @Autowired private MockMvc mockMvc;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CategoryServiceImpl categoryService;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductServiceImpl productService;
    @Autowired private CartRepository cartRepository;
    @Autowired private UserService userService;
    @Autowired private CartService cartService;

    /** Set up before each test */
    @BeforeEach
    void setUp() {
        userService.createUser(userDto);

        CategoryDto categoryDto1 = CategoryDto.builder().categoryName("category1").build();
        CategoryDto categoryDto2 = CategoryDto.builder().categoryName("category2").build();
        CategoryDto categoryDto3 = CategoryDto.builder().categoryName("category3").build();

        categoryService.createCategory(categoryDto1);
        categoryService.createCategory(categoryDto2);
        categoryService.createCategory(categoryDto3);

        categoryRepository.flush();

        List<Category> categories = categoryRepository.findAll();
        Long cartegoryId1 = categories.get(0).getId();
        Long cartegoryId2 = categories.get(1).getId();
        Long cartegoryId3 = categories.get(2).getId();

        categoryDto1.setId(cartegoryId1);
        categoryDto2.setId(cartegoryId2);
        categoryDto3.setId(cartegoryId3);

        var productDto1 =
                ProductDto.builder()
                        .name("product1")
                        .price(100)
                        .quantity(15)
                        .imageUrl("image1")
                        .category(categoryDto1)
                        .build();
        var productDto2 =
                ProductDto.builder()
                        .name("product2")
                        .price(200)
                        .quantity(10)
                        .imageUrl("image2")
                        .category(categoryDto2)
                        .build();
        var productDto3 =
                ProductDto.builder()
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

        // Create Cart
        CartRequest cartRequest = CartRequest.builder().userId(userDto.getId()).build();
        cart = CartMapper.toEntity(cartService.createCart(cartRequest).getCart());

        assert cart != null;

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
     * @throws Exception if the test fails
     */
    @Test
    @Rollback
    void orderApiTest() throws Exception {
        // prepare request data
        List<Long> cartItemIds = List.of(1L, 2L, 3L);
        LocalDateTime orderDate = LocalDateTime.now();
        OrderRequest orderRequest =
                OrderRequest.builder()
                        .userId(1L)
                        .cartItemIds(cartItemIds)
                        .orderDate(orderDate)
                        .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        var insertData = objectMapper.writeValueAsString(orderRequest);

        // perform request
        mockMvc.perform(
                        post("/api/order/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData)
                                .header("Authorization", userBasicAuth))
                .andExpect(status().isOk());

        // prepare request data
        orderRequest = OrderRequest.builder().orderId(1L).status(OrderStatus.DELIVERED).build();
        insertData = new ObjectMapper().writeValueAsString(orderRequest);

        // check change-status api is working
        mockMvc.perform(
                        get("/api/order/change-status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData))
                .andExpect(status().isOk());

        // check get api is working and also check the status-change worked
        mockMvc.perform(get("/api/order/get/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order.id").value(1))
                .andExpect(jsonPath("$.order.user.id").value(1))
                .andExpect(jsonPath("$.order.status").value(OrderStatus.DELIVERED.name()));

        // bad request
        // -> Order not found
        mockMvc.perform(get("/api/order/get/1/2")).andExpect(status().is4xxClientError());

        // prepare request data
        orderRequest =
                OrderRequest.builder().requestType(RequestType.REQUEST_RETURN).orderId(1L).build();
        insertData = new ObjectMapper().writeValueAsString(orderRequest);

        // check request api is working
        mockMvc.perform(
                        get("/api/order/request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/order/get/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.order.status").value(OrderStatus.RETURN_REQUESTED.name()));

        // make bad request
        // -> request type is not valid
        mockMvc.perform(
                        get("/api/order/request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData))
                .andExpect(status().is4xxClientError());
    }
}
