package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.bit.joe.shoppingmall.dto.CategoryDto;
import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.entity.Category;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.mapper.CategoryMapper;
import com.bit.joe.shoppingmall.repository.CategoryRepository;
import com.bit.joe.shoppingmall.repository.ProductRepository;
import com.bit.joe.shoppingmall.repository.UserRepository;
import com.bit.joe.shoppingmall.service.Impl.CategoryServiceImpl;
import com.bit.joe.shoppingmall.service.Impl.ProductServiceImpl;
import com.bit.joe.shoppingmall.service.Impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@Transactional
public class CategoryControllerTests {
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
    @PersistenceContext private EntityManager entityManager;
    @Autowired private ProductServiceImpl productService;
    @Autowired private ProductRepository productRepository;

    @BeforeAll
    static void setUpContainer() {
        mySQLContainer.start();
    }

    @AfterAll
    static void stopContainer() {
        mySQLContainer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.jpa.show-sql", () -> "true");
    }

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        userService.createUser(userDto);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();

        userRepository.flush();
        categoryRepository.flush();

        entityManager.clear();
    }

    @Test
    @Order(1)
    void createCategory() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setCategoryName("Test Category");

        ObjectMapper objectMapper = new ObjectMapper();
        String contentJson = objectMapper.writeValueAsString(categoryDto);

        mockMvc.perform(
                        post("/category/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader)
                                .content(contentJson)
                                .session(mockHttpSession))
                .andExpect(status().isOk());
        var repo = categoryRepository.findAll();
        Assertions.assertEquals(1, repo.size());
    }

    @Test
    @Order(2)
    @Transactional
    void getCategories() throws Exception {
        // Get All Categories
        mockMvc.perform(get("/category/get-all")).andExpect(status().isOk());

        // Get Single Category
        Category category = new Category();
        category.setId(1L);
        category.setCategoryName("Test Category");
        categoryService.createCategory(CategoryMapper.categoryToDto(category));

        mockMvc.perform(get("/category/get-all")).andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void deleteCategory() throws Exception {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("Test Category");

        ObjectMapper objectMapper = new ObjectMapper();
        String contentJson = objectMapper.writeValueAsString(categoryDto);

        mockMvc.perform(
                        post("/category/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader)
                                .content(contentJson)
                                .session(mockHttpSession))
                .andExpect(status().isOk());

        var createdCategory = categoryRepository.findAll();
        Assertions.assertEquals(1, createdCategory.size(), "카테고리가 정상적으로 생성되어야 합니다.");

        Long categoryId = createdCategory.get(0).getId();

    @Test
    void deleteCategory() throws Exception {
        var category = Category.builder().id(1L).categoryName("Test Category").build();

        // insert Data
        mockMvc.perform(
                        post("/category/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader)
                                .content(
                                        new ObjectMapper()
                                                .writeValueAsString(
                                                        CategoryMapper.categoryToDto(category)))
                                .session(mockHttpSession))
                .andExpect(status().isOk());

        // delete data
        mockMvc.perform(
                        delete("/category/delete/" + categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader)
                                .session(mockHttpSession))
                .andExpect(status().isOk()); // 삭제가 정상적으로 이루어져야 함.

        var deletedCategory = categoryRepository.findById(categoryId);
        Assertions.assertFalse(deletedCategory.isPresent(), "삭제된 카테고리는 존재하지 않아야 합니다.");

        try {
            mockMvc.perform(get("/category/get-category-by-id/" + categoryId))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            System.out.println("Expected error occurred: " + e.getMessage());
        }
    }
}
