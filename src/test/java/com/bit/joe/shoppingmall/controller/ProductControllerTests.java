package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.bit.joe.shoppingmall.dto.*;
import com.bit.joe.shoppingmall.dto.request.ProductRequest;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.repository.CategoryRepository;
import com.bit.joe.shoppingmall.repository.UserRepository;
import com.bit.joe.shoppingmall.service.Impl.CategoryServiceImpl;
import com.bit.joe.shoppingmall.service.Impl.ProductServiceImpl;
import com.bit.joe.shoppingmall.service.Impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@ExtendWith({SpringExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTests {
    @Container public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:lts");
    UserDto userDto =
            UserDto.builder()
                    .name("admin")
                    .password("admin")
                    .email("admin@example.com")
                    .role(UserRole.ADMIN)
                    .gender(UserGender.MALE)
                    .birth("2021-01-01")
                    .build();
    String basicAuthHeader =
            "Basic "
                    + Base64.getEncoder()
                            .encodeToString(
                                    (userDto.getEmail() + userDto.getPassword()).getBytes());
    MockHttpSession mockHttpSession = new MockHttpSession();
    private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserServiceImpl userService;
    @Autowired private CategoryServiceImpl categoryService;
    @Autowired private HttpSession session;
    @Autowired private CategoryController categoryController;
    @Autowired private ProductController productController;
    @Autowired private ProductServiceImpl productService;
    @PersistenceContext private EntityManager entityManager;

    @BeforeAll
    static void setUpContainer() {
        mySQLContainer.start();
    }

    @AfterAll
    static void stopContainer() {
        mySQLContainer.stop();
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userService.createUser(userDto);
        categoryRepository.deleteAll();

        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        userRepository.flush();
        entityManager.flush();
    }

    @Test
    void adminCreateProduct() throws Exception {
        categoryService.createCategory(CategoryDto.builder().categoryName("Test Category").build());

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
                                .header("Authorization", basicAuthHeader)
                                .content(contentJson))
                .andExpect(status().isOk());
    }

    @Test
    void adminCreateDuplicateProduct() throws Exception {
        categoryService.createCategory(CategoryDto.builder().categoryName("Test Category").build());

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
                                .header("Authorization", basicAuthHeader)
                                .content(contentJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void userCreateProduct() throws Exception {
        categoryService.createCategory(CategoryDto.builder().categoryName("Test Category").build());

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
                                .content(contentJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testAdminCreateProductWithEmptyBody() throws Exception {
        ProductRequest productRequest = new ProductRequest();

        ObjectMapper objectMapper = new ObjectMapper();
        String contentJson = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(
                        post("/product/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader)
                                .content(contentJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testGetProducts() throws Exception {
        categoryService.createCategory(CategoryDto.builder().categoryName("Test Category").build());
        productService.createProduct(1L, "image", "product", 10, 1000);

        mockMvc.perform(get("/product/get-all")).andExpect(status().isOk());
        mockMvc.perform(get("/product/get-by-product-id/1")).andExpect(status().isOk());
        mockMvc.perform(get("/product/get-by-category-id/1")).andExpect(status().isOk());

        mockMvc.perform(get("/product/get-by-product-id/2")).andExpect(status().is4xxClientError());
        mockMvc.perform(get("/product/get-by-category-id/2"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void adminUpdateProduct() throws Exception {
        categoryService.createCategory(CategoryDto.builder().categoryName("Test Category").build());
        productService.createProduct(1L, "image", "product", 10, 1000);

        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("product2");
        productRequest.setPrice(1000);
        productRequest.setQuantity(11);
        productRequest.setImage("image");
        productRequest.setCategoryId(1L);

        ObjectMapper objectMapper = new ObjectMapper();
        String contentJson = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(
                        put("/product/update/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader)
                                .content(contentJson))
                .andExpect(status().isOk());

        // Return bad request because quantity is negative
        productRequest.setQuantity(-1);
        mockMvc.perform(
                        put("/product/update/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader)
                                .content(contentJson))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/product/get-by-product-id/1"))
                .andExpect(jsonPath("$.product[?(@.id == 1)].categoryName").value("product2"));
    }

    @Test
    void deleteProduct() throws Exception {
        categoryService.createCategory(
                CategoryDto.builder().id(1L).categoryName("Test Category").build());
        categoryRepository.flush();
        productService.createProduct(1L, "image", "product", 10, 1000);

        mockMvc.perform(
                        delete("/product/delete/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader))
                .andExpect(status().isOk());
    }
}
