package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.bit.joe.shoppingmall.dto.*;
import com.bit.joe.shoppingmall.dto.request.CartItemRequest;
import com.bit.joe.shoppingmall.dto.request.CartRequest;
import com.bit.joe.shoppingmall.dto.request.OrderRequest;
import com.bit.joe.shoppingmall.entity.Product;
import com.bit.joe.shoppingmall.enums.RequestType;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.jwt.JWTUtil;
import com.bit.joe.shoppingmall.repository.CartRepository;
import com.bit.joe.shoppingmall.repository.CategoryRepository;
import com.bit.joe.shoppingmall.repository.ProductRepository;
import com.bit.joe.shoppingmall.service.CartItemService;
import com.bit.joe.shoppingmall.service.Impl.CartService;
import com.bit.joe.shoppingmall.service.Impl.CategoryServiceImpl;
import com.bit.joe.shoppingmall.service.Impl.OrderService;
import com.bit.joe.shoppingmall.service.Impl.ProductServiceImpl;
import com.bit.joe.shoppingmall.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.http.Cookie;
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
                    .email("user for order controller test")
                    .role(UserRole.USER)
                    .gender(UserGender.MALE)
                    .birth("2021-01-01")
                    .build();

    Cookie cookie;
    String userJwtToken;

    CartDto cartDto;

    CategoryDto categoryDto1 = CategoryDto.builder().categoryName("category1").build();
    CategoryDto categoryDto2 = CategoryDto.builder().categoryName("category2").build();
    CategoryDto categoryDto3 = CategoryDto.builder().categoryName("category3").build();

    Product product1;
    Product product2;
    Product product3;

    ProductDto productDto1 =
            ProductDto.builder()
                    .name("product1")
                    .price(100)
                    .quantity(15)
                    .imageUrl("image1")
                    .category(categoryDto1)
                    .build();
    ProductDto productDto2 =
            ProductDto.builder()
                    .name("product2")
                    .price(200)
                    .quantity(10)
                    .imageUrl("image2")
                    .category(categoryDto2)
                    .build();
    ProductDto productDto3 =
            ProductDto.builder()
                    .name("product3")
                    .price(300)
                    .quantity(5)
                    .imageUrl("image3")
                    .category(categoryDto3)
                    .build();

    OrderDto orderDto;

    @Autowired private MockMvc mockMvc;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CategoryServiceImpl categoryService;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductServiceImpl productService;
    @Autowired private CartRepository cartRepository;
    @Autowired private UserService userService;
    @Autowired private CartService cartService;
    @Autowired private OrderService orderService;
    @Autowired private JWTUtil jwtUtil;
    @Autowired private CartItemService cartItemService;
    @Autowired private ObjectMapper objectMapper;

    /** Set up before each test */
    @BeforeEach
    void setUp() {
        userService.createUser(userDto);
        userDto = userService.getUserByEmail(userDto.getEmail()).getUser();
        assert userDto.getId() != null;

        // create cookie
        userJwtToken = jwtUtil.createJwt(userDto.getEmail(), userDto.getRole().name(), 36000000L);
        cookie = new Cookie("token", userJwtToken);

        categoryDto1 = categoryService.createCategory(categoryDto1).getCategory();
        categoryDto2 = categoryService.createCategory(categoryDto2).getCategory();
        categoryDto3 = categoryService.createCategory(categoryDto3).getCategory();

        categoryRepository.flush();

        productDto1.setCategory(categoryDto1);
        productDto2.setCategory(categoryDto2);
        productDto3.setCategory(categoryDto3);

        productDto1 =
                productService
                        .createProduct(
                                productDto1.getCategory().getId(),
                                productDto1.getImageUrl(),
                                productDto1.getName(),
                                productDto1.getQuantity(),
                                productDto1.getPrice())
                        .getProduct();
        productDto2 =
                productService
                        .createProduct(
                                productDto2.getCategory().getId(),
                                productDto2.getImageUrl(),
                                productDto2.getName(),
                                productDto2.getQuantity(),
                                productDto2.getPrice())
                        .getProduct();
        productDto3 =
                productService
                        .createProduct(
                                productDto3.getCategory().getId(),
                                productDto3.getImageUrl(),
                                productDto3.getName(),
                                productDto3.getQuantity(),
                                productDto3.getPrice())
                        .getProduct();

        productRepository.flush();

        // Create Cart
        CartRequest cartRequest = CartRequest.builder().userId(userDto.getId()).build();
        cartDto = cartService.createCart(cartRequest).getCart();
        assert cartDto != null;

        CartItemRequest cartItemRequest1 =
                CartItemRequest.builder()
                        .cartId(cartDto.getId())
                        .productId(productDto1.getId())
                        .quantity(1)
                        .build();
        CartItemRequest cartItemRequest2 =
                CartItemRequest.builder()
                        .cartId(cartDto.getId())
                        .productId(productDto2.getId())
                        .quantity(2)
                        .build();
        CartItemRequest cartItemRequest3 =
                CartItemRequest.builder()
                        .cartId(cartDto.getId())
                        .productId(productDto3.getId())
                        .quantity(3)
                        .build();

        // create cart items
        cartItemService.addCartItem(cartItemRequest1);
        cartItemService.addCartItem(cartItemRequest2);
        cartItemService.addCartItem(cartItemRequest3);

        List<Long> cartItemIds = List.of();
        LocalDateTime orderDate = LocalDateTime.now();
        OrderRequest orderRequest =
                OrderRequest.builder().cartItemIds(cartItemIds).orderDate(orderDate).build();

        orderDto = orderService.createOrder(userJwtToken, orderRequest).getOrder();
    }

    /**
     * Test create order
     *
     * @throws Exception if the test fails
     */
    @Test
    void cartCreateTest() throws Exception {
        // prepare request data
        List<Long> cartItemIds = List.of();
        LocalDateTime orderDate = LocalDateTime.now();
        OrderRequest orderRequest =
                OrderRequest.builder().cartItemIds(cartItemIds).orderDate(orderDate).build();
        objectMapper.registerModule(new JavaTimeModule());
        var insertData = objectMapper.writeValueAsString(orderRequest);

        // perform request
        mockMvc.perform(
                        post("/api/order/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(cookie)
                                .content(insertData))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Order request test: cancle request")
    void orderRequestTest() throws Exception {
        // prepare request data
        OrderRequest orderRequest =
                OrderRequest.builder()
                        .orderId(orderDto.getId())
                        .requestType(RequestType.REQUEST_CANCEL)
                        .build();
        objectMapper.registerModule(new JavaTimeModule());
        var insertData = objectMapper.writeValueAsString(orderRequest);

        // perform request
        mockMvc.perform(
                        get("/api/order/request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(cookie)
                                .content(insertData))
                .andExpect(status().isOk());


        // bad request test (invalid request type) ========================================
        // prepare request data
        orderRequest =
            OrderRequest.builder()
                .orderId(orderDto.getId())
                .requestType(RequestType.COMPLETE_RETURN)
                .build();
        objectMapper.registerModule(new JavaTimeModule());
        insertData = objectMapper.writeValueAsString(orderRequest);

        // perform request
        mockMvc.perform(
                get("/api/order/request")
                    .contentType(MediaType.APPLICATION_JSON)
                    .cookie(cookie)
                    .content(insertData))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Order Get Test")
    void getOrderTest() throws Exception {
        // perform request
        mockMvc.perform(
                        get("/api/order/get/" + orderDto.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Order Get All Test")
    void getAllOrderTest() throws Exception {
        // perform request
        mockMvc.perform(
                        get("/api/order/get-all")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(cookie))
                .andExpect(status().isOk());
    }
}
