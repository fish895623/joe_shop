package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bit.joe.shoppingmall.dto.*;
import com.bit.joe.shoppingmall.dto.request.ProductRequest;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.mapper.UserMapper;
import com.bit.joe.shoppingmall.repository.CategoryRepository;
import com.bit.joe.shoppingmall.repository.ProductRepository;
import com.bit.joe.shoppingmall.repository.UserRepository;
import com.bit.joe.shoppingmall.service.Impl.CategoryServiceImpl;
import com.bit.joe.shoppingmall.service.Impl.ProductServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource("classpath:application-test.properties")
public class ProductControllerTests {
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
    private MockMvc mockMvc;

    @Autowired private HttpSession session;

    @Autowired private CategoryController categoryController;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CategoryServiceImpl categoryService;
    @Autowired private ProductController productController;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductServiceImpl productService;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        var categoryDto = CategoryDto.builder().id(1L).categoryName("category").build();
        categoryService.createCategory(categoryDto);
        userRepository.save(UserMapper.toEntity(userDto));
        userRepository.save(UserMapper.toEntity(adminDto));
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService)).build();
    }

    @Test
    @Order(1)
    void adminCreateProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("product");
        productRequest.setPrice(1000);
        productRequest.setQuantity(10);
        productRequest.setImage("image");
        productRequest.setCategoryId(1L);

        String contentJson = new ObjectMapper().writeValueAsString(productRequest);

        mockMvc.perform(
                        post("/product/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", adminBasicAuth)
                                .content(contentJson))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void adminCreateDuplicateProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("product");
        productRequest.setPrice(1000);
        productRequest.setQuantity(10);
        productRequest.setImage("image");
        productRequest.setCategoryId(1L);

        String contentJson = new ObjectMapper().writeValueAsString(productRequest);

        mockMvc.perform(
                        post("/product/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", adminBasicAuth)
                                .content(contentJson))
                .andExpect(status().isOk());

        // 두 번째 요청: 동일한 제품을 생성 시도 (중복된 제품 생성)
        mockMvc.perform(
                        post("/product/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", adminBasicAuth)
                                .content(contentJson))
                .andExpect(status().is4xxClientError()) // 중복 -> expect to 400 Bad Request
                .andExpect(
                        jsonPath("$.message")
                                .value("Product name must be unique within the same category."));
    }

    /** User cannot create product */
    @Test
    @Order(3)
    void userCreateProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("product");
        productRequest.setPrice(1000);
        productRequest.setQuantity(10);
        productRequest.setImage("image");
        productRequest.setCategoryId(1L);

        ObjectMapper objectMapper = new ObjectMapper();
        String contentJson = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(
                        post("/product/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(contentJson)
                                .header("Authorization", userBasicAuth))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(4)
    void adminCreateProductWithEmptyBody() throws Exception {
        ProductRequest productRequest = new ProductRequest(); // 빈 요청 본문

        ObjectMapper objectMapper = new ObjectMapper();
        String contentJson = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(
                        post("/product/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", adminBasicAuth) // 인증 추가
                                .content(contentJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(5)
    void getProducts() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("product");
        productRequest.setPrice(1000);
        productRequest.setQuantity(10);
        productRequest.setImage("image");
        productRequest.setCategoryId(1L);

        productService.createProduct(
                productRequest.getCategoryId(),
                productRequest.getImage(),
                productRequest.getName(),
                productRequest.getQuantity(),
                productRequest.getPrice());

        // Test
        // 제품 목록 조회
        mockMvc.perform(get("/product/get-all")).andExpect(status().isOk());

        // 특정 제품 ID로 조회
        mockMvc.perform(get("/product/get-by-product-id/1")).andExpect(status().isOk());

        // 카테고리 ID로 제품 조회
        mockMvc.perform(get("/product/get-by-category-id/1")).andExpect(status().isOk());

        mockMvc.perform(get("/product/get-by-product-id/2")).andExpect(status().is4xxClientError());
        mockMvc.perform(get("/product/get-by-category-id/2"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @Order(6)
    void adminUpdateProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("product");
        productRequest.setPrice(1000);
        productRequest.setQuantity(10);
        productRequest.setImage("image");
        productRequest.setCategoryId(1L);

        productService.createProduct(
                productRequest.getCategoryId(),
                productRequest.getImage(),
                productRequest.getName(),
                productRequest.getQuantity(),
                productRequest.getPrice());

        // Update product information
        productRequest.setName("product2");

        String contentJson = new ObjectMapper().writeValueAsString(productRequest);
        mockMvc.perform(
                        put("/product/update/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", adminBasicAuth)
                                .content(contentJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/product/get-by-product-id/1"))
                .andExpect(jsonPath("$.product.name").value("product2"));
    }

    @Test
    @Order(7)
    void deleteProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("product");
        productRequest.setPrice(1000);
        productRequest.setQuantity(10);
        productRequest.setImage("image");
        productRequest.setCategoryId(1L);

        productService.createProduct(
                productRequest.getCategoryId(),
                productRequest.getImage(),
                productRequest.getName(),
                productRequest.getQuantity(),
                productRequest.getPrice());

        mockMvc.perform(
                        delete("/product/delete/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", adminBasicAuth))
                .andExpect(status().isOk());
    }
}
