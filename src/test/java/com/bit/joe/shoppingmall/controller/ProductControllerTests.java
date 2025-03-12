package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.bit.joe.shoppingmall.dto.*;
import com.bit.joe.shoppingmall.dto.request.ProductRequest;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.jwt.JWTUtil;
import com.bit.joe.shoppingmall.service.Impl.CategoryServiceImpl;
import com.bit.joe.shoppingmall.service.Impl.ProductServiceImpl;
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
public class ProductControllerTests {
    UserDto adminDto =
            UserDto.builder()
                    .name("admin")
                    .password("admin")
                    .email("admin Product")
                    .role(UserRole.ADMIN)
                    .gender(UserGender.MALE)
                    .birth("2021-01-01")
                    .build();
    UserDto userDto =
            UserDto.builder()
                    .name("user")
                    .password("user")
                    .email("user Product")
                    .role(UserRole.USER)
                    .gender(UserGender.MALE)
                    .birth("2021-01-01")
                    .build();
    String adminJwtToken;
    String userJwtToken;
    @Autowired private MockMvc mockMvc;
    @Autowired private CategoryServiceImpl categoryService;
    @Autowired private UserService userService;
    @Autowired private ProductServiceImpl productService;
    @Autowired private JWTUtil jwtUtil;
    private Long savedCategoryId;

    @BeforeEach
    void setUp() {

        CategoryDto categoryDto = CategoryDto.builder().categoryName("category").build();
        Response resp = categoryService.createCategory(categoryDto);
        savedCategoryId = resp.getCategory().getId();

        userService.createUser(adminDto);
        userService.createUser(userDto);

        adminJwtToken =
                jwtUtil.createJwt(adminDto.getEmail(), adminDto.getRole().toString(), 36000000L);
        userJwtToken =
                jwtUtil.createJwt(userDto.getEmail(), userDto.getRole().toString(), 36000000L);
    }

    @Test
    void adminCreateProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("product");
        productRequest.setPrice(1000);
        productRequest.setQuantity(10);
        productRequest.setImage("image");
        productRequest.setCategoryId(savedCategoryId);

        String contentJson = new ObjectMapper().writeValueAsString(productRequest);

        var cookie = new Cookie("token", adminJwtToken);
        cookie.setPath("/");

        mockMvc.perform(
                        post("/product/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(cookie)
                                .content(contentJson))
                .andExpect(status().isOk());
    }

    @Test
    void adminCreateDuplicateProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("product");
        productRequest.setPrice(1000);
        productRequest.setQuantity(10);
        productRequest.setImage("image");
        productRequest.setCategoryId(savedCategoryId);

        String contentJson = new ObjectMapper().writeValueAsString(productRequest);

        var cookie = new Cookie("token", adminJwtToken);
        cookie.setPath("/");

        mockMvc.perform(
                        post("/product/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(cookie)
                                .content(contentJson))
                .andExpect(status().isOk());

        // 두 번째 요청: 동일한 제품을 생성 시도 (중복된 제품 생성)
        mockMvc.perform(
                        post("/product/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(cookie)
                                .content(contentJson))
                .andExpect(status().is4xxClientError()) // 중복 -> expect to 400 Bad Request
                .andExpect(
                        jsonPath("$.message")
                                .value("Product name must be unique within the same category."));
    }

    /** User cannot create product */
    @Test
    void userCreateProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("product");
        productRequest.setPrice(1000);
        productRequest.setQuantity(10);
        productRequest.setImage("image");
        productRequest.setCategoryId(savedCategoryId);

        ObjectMapper objectMapper = new ObjectMapper();
        String contentJson = objectMapper.writeValueAsString(productRequest);

        var cookie = new Cookie("token", userJwtToken);
        cookie.setPath("/");

        mockMvc.perform(
                        post("/product/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(contentJson)
                                .cookie(cookie))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void adminCreateProductWithEmptyBody() throws Exception {
        ProductRequest productRequest = new ProductRequest(); // 빈 요청 본문

        ObjectMapper objectMapper = new ObjectMapper();
        String contentJson = objectMapper.writeValueAsString(productRequest);

        var cookie = new Cookie("token", adminJwtToken);
        cookie.setPath("/");

        mockMvc.perform(
                        post("/product/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .cookie(cookie)
                                .content(contentJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getProducts() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("product");
        productRequest.setPrice(1000);
        productRequest.setQuantity(10);
        productRequest.setImage("image");
        productRequest.setCategoryId(savedCategoryId);

        Response resp =
                productService.createProduct(
                        productRequest.getCategoryId(),
                        productRequest.getImage(),
                        productRequest.getName(),
                        productRequest.getQuantity(),
                        productRequest.getPrice());

        Long createdproductId = resp.getProduct().getId();
        Long categoryIdOfcreatedProduct = resp.getProduct().getCategory().getId();

        // Test
        // 제품 목록 조회
        mockMvc.perform(get("/product/get-all")).andExpect(status().isOk());

        // 특정 제품 ID로 조회
        mockMvc.perform(get("/product/get-by-product-id/" + createdproductId))
                .andExpect(status().isOk());

        // 카테고리 ID로 제품 조회
        mockMvc.perform(get("/product/get-by-category-id/" + categoryIdOfcreatedProduct))
                .andExpect(status().isOk());

        // 존재하지 않는 제품 ID, 카테고리 ID로 조회
        mockMvc.perform(get("/product/get-by-product-id/" + createdproductId + 1))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(get("/product/get-by-category-id/" + categoryIdOfcreatedProduct + 1))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void adminUpdateProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("product");
        productRequest.setPrice(1000);
        productRequest.setQuantity(10);
        productRequest.setImage("image");
        productRequest.setCategoryId(savedCategoryId);

        Response resp =
                productService.createProduct(
                        productRequest.getCategoryId(),
                        productRequest.getImage(),
                        productRequest.getName(),
                        productRequest.getQuantity(),
                        productRequest.getPrice());

        // Update product information
        productRequest.setName("product2");

        // Created product ID
        Long createdproductId = resp.getProduct().getId();

        String contentJson = new ObjectMapper().writeValueAsString(productRequest);
        mockMvc.perform(
                        put("/product/update/" + createdproductId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(contentJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/product/get-by-product-id/" + createdproductId))
                .andExpect(jsonPath("$.product.name").value("product2"));
    }

    @Test
    void deleteProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("product");
        productRequest.setPrice(1000);
        productRequest.setQuantity(10);
        productRequest.setImage("image");
        productRequest.setCategoryId(savedCategoryId);

        Response resp =
                productService.createProduct(
                        productRequest.getCategoryId(),
                        productRequest.getImage(),
                        productRequest.getName(),
                        productRequest.getQuantity(),
                        productRequest.getPrice());

        Long createdproductId = resp.getProduct().getId();

        mockMvc.perform(
                        delete("/product/delete/" + createdproductId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
