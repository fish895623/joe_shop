// package com.bit.joe.shoppingmall.controller;
//
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
// import java.util.Base64;
//
// import org.junit.jupiter.api.*;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.mock.web.MockHttpSession;
// import org.springframework.test.context.junit.jupiter.SpringExtension;
// import org.springframework.test.web.servlet.MockMvc;
// import org.testcontainers.containers.MySQLContainer;
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;
//
// import com.bit.joe.shoppingmall.dto.UserDto;
// import com.bit.joe.shoppingmall.dto.request.ProductRequest;
// import com.bit.joe.shoppingmall.enums.UserGender;
// import com.bit.joe.shoppingmall.enums.UserRole;
// import com.bit.joe.shoppingmall.repository.CategoryRepository;
// import com.bit.joe.shoppingmall.repository.UserRepository;
// import com.bit.joe.shoppingmall.service.Impl.CategoryServiceImpl;
// import com.bit.joe.shoppingmall.service.Impl.UserServiceImpl;
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// import jakarta.persistence.EntityManager;
// import jakarta.persistence.PersistenceContext;
// import jakarta.servlet.http.HttpSession;
// import jakarta.transaction.Transactional;
//
// @ExtendWith({SpringExtension.class})
// @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @Testcontainers
// @AutoConfigureMockMvc
// @Transactional
// public class ProductControllerTests {
//    @Container public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:lts");
//    UserDto userDto =
//            UserDto.builder()
//                    .name("admin")
//                    .password("admin")
//                    .email("admin@example.com")
//                    .role(UserRole.ADMIN)
//                    .gender(UserGender.MALE)
//                    .birth("2021-01-01")
//                    .build();
//    String basicAuthHeader =
//            "Basic "
//                    + Base64.getEncoder()
//                            .encodeToString(
//                                    (userDto.getEmail() + userDto.getPassword()).getBytes());
//    MockHttpSession mockHttpSession = new MockHttpSession();
//    private MockMvc mockMvc;
//    @Autowired private UserRepository userRepository;
//    @Autowired private CategoryRepository categoryRepository;
//    @Autowired private UserServiceImpl userService;
//    @Autowired private CategoryServiceImpl categoryService;
//    @Autowired private HttpSession session;
//    @Autowired private CategoryController categoryController;
//    @PersistenceContext private EntityManager entityManager;
//
//    @BeforeAll
//    static void setUpContainer() {
//        mySQLContainer.start();
//    }
//
//    @AfterAll
//    static void stopContainer() {
//        mySQLContainer.stop();
//    }
//
//    @Test
//    void testAdminCreateProduct() throws Exception {
//        ProductRequest productRequest = new ProductRequest();
//        productRequest.setName("product");
//        productRequest.setPrice(1000);
//        productRequest.setQuantity(10);
//        productRequest.setImage("image");
//        productRequest.setCategoryId(1L);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String contentJson = objectMapper.writeValueAsString(productRequest);
//
//        mockMvc.perform(
//                        post("/product/create")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .header("Authorization", basicAuthHeader)
//                                .content(contentJson))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testAdminCreateDuplicateProduct() throws Exception {
//        ProductRequest productRequest = new ProductRequest();
//        productRequest.setName("product");
//        productRequest.setPrice(1000);
//        productRequest.setQuantity(10);
//        productRequest.setImage("image");
//        productRequest.setCategoryId(1L);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String contentJson = objectMapper.writeValueAsString(productRequest);
//
//        mockMvc.perform(
//                        post("/product/create")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .header("Authorization", basicAuthHeader)
//                                .content(contentJson))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void testUserCreateProduct() throws Exception {
//        ProductRequest productRequest = new ProductRequest();
//        productRequest.setName("product");
//        productRequest.setPrice(1000);
//        productRequest.setQuantity(10);
//        productRequest.setImage("image");
//        productRequest.setCategoryId(1L);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String contentJson = objectMapper.writeValueAsString(productRequest);
//
//        mockMvc.perform(
//                        post("/product/create")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(contentJson))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void testAdminCreateProductWithEmptyBody() throws Exception {
//        ProductRequest productRequest = new ProductRequest();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String contentJson = objectMapper.writeValueAsString(productRequest);
//
//        mockMvc.perform(
//                        post("/product/create")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .header("Authorization", basicAuthHeader)
//                                .content(contentJson))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testGetProducts() throws Exception {
//        mockMvc.perform(get("/product/get-all")).andExpect(status().isOk());
//        mockMvc.perform(get("/product/get-by-product-id/1")).andExpect(status().isOk());
//        mockMvc.perform(get("/product/get-by-category-id/1")).andExpect(status().isOk());
//    }
//
//    @Test
//    void testAdminUpdateProduct() throws Exception {
//        ProductRequest productRequest = new ProductRequest();
//        productRequest.setName("product2");
//        productRequest.setPrice(1000);
//        productRequest.setQuantity(10);
//        productRequest.setImage("image");
//        productRequest.setCategoryId(1L);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String contentJson = objectMapper.writeValueAsString(productRequest);
//
//        mockMvc.perform(
//                        put("/product/update/1")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .header("Authorization", basicAuthHeader)
//                                .content(contentJson))
//                .andExpect(status().isOk());
//
//        productRequest.setQuantity(-1);
//
//        // Return bad request because quantity is negative
//        mockMvc.perform(
//                        put("/product/update/1")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .header("Authorization", basicAuthHeader)
//                                .content(contentJson))
//                .andExpect(status().isBadRequest());
//
//        mockMvc.perform(get("/product/get-by-product-id/1"))
//                .andExpect(jsonPath("$.product[?(@.id == 1)].categoryName").value("product2"));
//    }
//
//    @Test
//    void testDeleteProduct() throws Exception {
//        mockMvc.perform(
//                        delete("/product/delete/1")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .header("Authorization", basicAuthHeader))
//                .andExpect(status().isOk());
//    }
// }
