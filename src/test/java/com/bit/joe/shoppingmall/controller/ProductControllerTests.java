package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.MySQLContainer;

import com.bit.joe.shoppingmall.config.MySQLContainerConfig;
import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.dto.request.ProductRequest;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.service.CategoryService;
import com.bit.joe.shoppingmall.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerTests {
    static final MySQLContainer<?> mysql = MySQLContainerConfig.getInstance();
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
    @Mock private ProductService productService;
    @Mock private CategoryService categoryService;
    @Mock private HttpSession session;
    @InjectMocks private ProductController productController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @Order(1)
    void testAdminCreateProduct() throws Exception {
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
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void testAdminCreateDuplicateProduct() throws Exception {
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
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void testUserCreateProduct() throws Exception {
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
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    void testAdminCreateProductWithEmptyBody() throws Exception {
        ProductRequest productRequest = new ProductRequest();

        ObjectMapper objectMapper = new ObjectMapper();
        String contentJson = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(
                        post("/product/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader)
                                .content(contentJson))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(5)
    void testGetProducts() throws Exception {
        mockMvc.perform(get("/product/get-all")).andExpect(status().isOk());
        mockMvc.perform(get("/product/get-by-product-id/1")).andExpect(status().isOk());
        mockMvc.perform(get("/product/get-by-category-id/1")).andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void testAdminUpdateProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("product");
        productRequest.setPrice(1000);
        productRequest.setQuantity(10);
        productRequest.setImage("image2");
        productRequest.setCategoryId(1L);

        ObjectMapper objectMapper = new ObjectMapper();
        String contentJson = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(
                        post("/product/update/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader)
                                .content(contentJson))
                .andExpect(status().isOk());

        mockMvc.perform(get("/product/get-by-product-id/1"))
                .andExpect(jsonPath("$.pro[?(@.id == 1)].categoryName").value(""));
    }

    @Test
    @Order(99)
    void testDeleteProduct() throws Exception {
        mockMvc.perform(post("/product/delete/1")).andExpect(status().isOk());
    }
}
