package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.dto.request.CartItemRequest;
import com.bit.joe.shoppingmall.entity.Cart;
import com.bit.joe.shoppingmall.entity.CartItem;
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
import com.bit.joe.shoppingmall.service.Impl.CartItemServiceImpl;
import com.bit.joe.shoppingmall.service.Impl.CartService;
import com.bit.joe.shoppingmall.service.Impl.CategoryServiceImpl;
import com.bit.joe.shoppingmall.service.Impl.ProductServiceImpl;

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
                    .email("admin@example.com")
                    .role(UserRole.ADMIN)
                    .gender(UserGender.MALE)
                    .birth("2021-01-01")
                    .build();
    UserDto userDto =
            UserDto.builder()
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
                                    (adminDto.getEmail() + ":" + adminDto.getPassword())
                                            .getBytes());
    String userBasicAuth =
            "Basic "
                    + Base64.getEncoder()
                            .encodeToString(
                                    (userDto.getEmail() + ":" + userDto.getPassword()).getBytes());
    MockHttpSession mockHttpSession = new MockHttpSession();
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CategoryServiceImpl categoryService;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductServiceImpl productService;
    @Autowired private CartItemController cartItemController;
    @Autowired private CartItemServiceImpl cartItemService;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartService cartService;

    @BeforeEach
    public void setUp() {

        // Create user
        User adminUser = userRepository.save(UserMapper.toEntity(adminDto));
        User generalUser = userRepository.save(UserMapper.toEntity(userDto));

        // Create Cart
        Cart cart = Cart.builder().user(generalUser).cartItems(List.<CartItem>of()).build();
        cartRepository.save(cart);

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
    void createCartItem() throws Exception {

        // test createCartItem

        // prepare request data
        CartItemRequest cartItemRequest =
                CartItemRequest.builder().cartId(1L).productId(1L).quantity(1).build();

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
                CartItemRequest.builder().cartId(1L).productId(1L).quantity(2).build();

        var updateData = new ObjectMapper().writeValueAsString(updateCartItemRequest);

        mockMvc.perform(
                        post("/api/cart-item/update")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItem.quantity").value(2));

        // =================================================================================================
        // test deleteCartItem

        // prepare request data
        CartItemRequest deleteCartItemRequest =
                CartItemRequest.builder().cartId(1L).productId(1L).build();

        var deleteData = new ObjectMapper().writeValueAsString(deleteCartItemRequest);

        mockMvc.perform(
                        get("/api/cart-item/delete")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(deleteData))
                .andExpect(status().isOk());

        // check is cartItem deleted successfully

        // prepare request data
        CartItemRequest getCartItemRequest =
                CartItemRequest.builder().userId(1L).productId(1L).build();

        var getData = new ObjectMapper().writeValueAsString(getCartItemRequest);

        mockMvc.perform(
                        get("/api/cart-item/get")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(getData))
                .andExpect(status().is4xxClientError());
    }
}
