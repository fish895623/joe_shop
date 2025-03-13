package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bit.joe.shoppingmall.repository.CartItemRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.dto.request.CartItemRequest;
import com.bit.joe.shoppingmall.dto.request.CartRequest;
import com.bit.joe.shoppingmall.entity.Cart;
import com.bit.joe.shoppingmall.entity.Category;
import com.bit.joe.shoppingmall.entity.Product;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.mapper.CartMapper;
import com.bit.joe.shoppingmall.mapper.CategoryMapper;
import com.bit.joe.shoppingmall.mapper.ProductMapper;
import com.bit.joe.shoppingmall.mapper.UserMapper;
import com.bit.joe.shoppingmall.service.Impl.CartService;
import com.bit.joe.shoppingmall.service.Impl.CategoryServiceImpl;
import com.bit.joe.shoppingmall.service.Impl.ProductServiceImpl;
import com.bit.joe.shoppingmall.service.UserService;

import jakarta.transaction.Transactional;

@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
@Rollback
public class CartItemControllerTests {
    UserDto adminDto =
            UserDto.builder()
                    .name("admin")
                    .password("admin")
                    .email("admin cartItem")
                    .role(UserRole.ADMIN)
                    .gender(UserGender.MALE)
                    .birth("2021-01-01")
                    .build();
    UserDto userDto =
            UserDto.builder()
                    .name("user")
                    .password("user")
                    .email("user cartItem")
                    .role(UserRole.USER)
                    .gender(UserGender.MALE)
                    .birth("2021-01-01")
                    .build();

    @Autowired private MockMvc mockMvc;
    @Autowired private CategoryServiceImpl categoryService;
    @Autowired private ProductServiceImpl productService;
    @Autowired private CartService cartService;
    @Autowired private UserService userService;

    private User generalUser;
    private User adminUser;
    private Cart cart;
    private Category category;
    private Product product;
    private Product product2;
    @Autowired
    private CartItemRepository cartItemRepository;

    @BeforeEach
    public void setUp() {

        // Create user
        userService.createUser(adminDto);
        userService.createUser(userDto);

        // get user data
        generalUser = UserMapper.toEntity(userService.getUserByEmail(userDto.getEmail()).getUser());
        adminUser = UserMapper.toEntity(userService.getUserByEmail(adminDto.getEmail()).getUser());

        assert generalUser != null;
        assert adminUser != null;

        // Create Cart
        CartRequest cartRequest = CartRequest.builder().userId(generalUser.getId()).build();
        cart = CartMapper.toEntity(cartService.createCart(cartRequest).getCart());

        assert cart != null;

        // Prepare data
        Category categoryForCreate =
                Category.builder().categoryName("Test Category for CartItem Test").build();
        category =
                CategoryMapper.toEntity(
                        categoryService
                                .createCategory(CategoryMapper.categoryToDto(categoryForCreate))
                                .getCategory());

        Product productForCreate =
                Product.builder()
                        .name("Test Product for CartItem Test")
                        .category(category)
                        .imageURL("image")
                        .quantity(10)
                        .price(1000)
                        .build();
        var productDto = ProductMapper.toDto(productForCreate);

        Product productForCreate2 =
                Product.builder()
                        .name("Test Product for CartItem Test 2")
                        .category(category)
                        .imageURL("image")
                        .quantity(10)
                        .price(1000)
                        .build();
        var productDto2 = ProductMapper.toDto(productForCreate2);

        product =
                ProductMapper.toEntity(
                        productService
                                .createProduct(
                                        productDto.getCategory().getId(),
                                        productDto.getImageUrl(),
                                        productDto.getName(),
                                        productDto.getQuantity(),
                                        productDto.getPrice())
                                .getProduct());

        product2 =
                ProductMapper.toEntity(
                        productService
                                .createProduct(
                                        productDto2.getCategory().getId(),
                                        productDto2.getImageUrl(),
                                        productDto2.getName(),
                                        productDto2.getQuantity(),
                                        productDto2.getPrice())
                                .getProduct());
    }

    @Test
    void createCartItem() throws Exception {

        // test createCartItem

        // prepare request data
        CartItemRequest cartItemRequest =
                CartItemRequest.builder()
                        .cartId(cart.getId())
                        .productId(product.getId())
                        .quantity(1)
                        .build();

        var insertData = new ObjectMapper().writeValueAsString(cartItemRequest);

        mockMvc.perform(
                        post("/api/cart-item/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData))
                .andExpect(status().isOk());

        // =================================================================================================
        // test updateCartItem

        // prepare request data
        CartItemRequest updateCartItemRequest =
                CartItemRequest.builder()
                        .cartId(cart.getId())
                        .productId(product.getId())
                        .quantity(2)
                        .build();

        var updateData = new ObjectMapper().writeValueAsString(updateCartItemRequest);

        mockMvc.perform(
                        post("/api/cart-item/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItem.quantity").value(2));

        // =================================================================================================
        // test getCartItem

        // prepare request data
        CartItemRequest getCartItemRequest =
                CartItemRequest.builder()
                        .userId(generalUser.getId())
                        .productId(product.getId())
                        .build();

        var getData = new ObjectMapper().writeValueAsString(getCartItemRequest);

        mockMvc.perform(
                        get("/api/cart-item/get")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItem.quantity").value(2));

        // =================================================================================================
        // test deleteCartItem

        // prepare request data
        CartItemRequest deleteCartItemRequest =
                CartItemRequest.builder().cartId(cart.getId()).productId(product.getId()).build();

        var deleteData = new ObjectMapper().writeValueAsString(deleteCartItemRequest);

        mockMvc.perform(
                        delete("/api/cart-item/delete")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(deleteData))
                .andExpect(status().isOk());

        // check is cartItem deleted successfully

        // prepare request data
        getCartItemRequest =
                CartItemRequest.builder()
                        .userId(generalUser.getId())
                        .productId(product.getId())
                        .build();

        getData = new ObjectMapper().writeValueAsString(getCartItemRequest);

        mockMvc.perform(
                        get("/api/cart-item/get")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getData))
                .andExpect(status().is4xxClientError());
    }

    // get-all
    @Test
    @DisplayName("Get all cart items")
    public void getAllCartItemsTest() throws Exception {
        // prepare request data
        CartItemRequest cartItemRequest =
                CartItemRequest.builder()
                        .cartId(cart.getId())
                        .productId(product.getId())
                        .quantity(5)
                        .build();
        var insertData = new ObjectMapper().writeValueAsString(cartItemRequest);

        mockMvc.perform(
                        post("/api/cart-item/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData))
                .andExpect(status().isOk());

        cartItemRequest = CartItemRequest.builder().userId(generalUser.getId()).build();
        insertData = new ObjectMapper().writeValueAsString(cartItemRequest);

        mockMvc.perform(
                        get("/api/cart-item/get-all")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItemList[0].quantity").value(5))
                .andExpect(jsonPath("$.cartItemList.length()").value(1));
    }

    // clear
    @Test
    @DisplayName("Clear cart items Test")
    public void clearCartTest() throws Exception {
        // prepare request data
        CartItemRequest cartItemRequest =
                CartItemRequest.builder()
                        .cartId(cart.getId())
                        .productId(product.getId())
                        .quantity(5)
                        .build();
        var insertData = new ObjectMapper().writeValueAsString(cartItemRequest);

        mockMvc.perform(
                        post("/api/cart-item/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData))
                .andExpect(status().isOk());

        // prepare request data
        cartItemRequest =
                CartItemRequest.builder()
                        .cartId(cart.getId())
                        .productId(product2.getId())
                        .quantity(15)
                        .build();
        insertData = new ObjectMapper().writeValueAsString(cartItemRequest);

        mockMvc.perform(
                        post("/api/cart-item/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData))
                .andExpect(status().isOk());

        cartItemRequest = CartItemRequest.builder().userId(generalUser.getId()).build();
        insertData = new ObjectMapper().writeValueAsString(cartItemRequest);

        mockMvc.perform(
                        get("/api/cart-item/get-all")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItemList[0].quantity").value(5))
                .andExpect(jsonPath("$.cartItemList[1].quantity").value(15))
                .andExpect(jsonPath("$.cartItemList.length()").value(2));

        mockMvc.perform(
                        post("/api/cart-item/clear")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(insertData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cart.cartItemDto.length()").value(0));
    }
}
