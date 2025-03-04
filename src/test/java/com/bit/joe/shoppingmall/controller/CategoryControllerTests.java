package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
import com.bit.joe.shoppingmall.repository.UserRepository;
import com.bit.joe.shoppingmall.service.Impl.CategoryServiceImpl;
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
    @PersistenceContext private EntityManager entityManager;

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
    void updateCategory() throws Exception {
        var category = Category.builder().id(1L).categoryName("Test Category").build();
        var categoryUpdate = Category.builder().id(1L).categoryName("Update Category").build();

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

        var repo = categoryRepository.findById(1L);
        Assertions.assertTrue(repo.isPresent());
        Assertions.assertEquals("Test Category", repo.get().getCategoryName());

        // update data
        mockMvc.perform(
                        put("/category/update/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader)
                                .content(
                                        new ObjectMapper()
                                                .writeValueAsString(
                                                        CategoryMapper.categoryToDto(
                                                                categoryUpdate)))
                                .session(mockHttpSession))
                .andExpect(status().isOk());

        mockMvc.perform(get("/category/get-category-by-id/1")).andExpect(status().isOk());

        repo = categoryRepository.findById(1L);
        Assertions.assertTrue(repo.isPresent());
        Assertions.assertEquals("Update Category", repo.get().getCategoryName());
    }

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
                        delete("/category/delete/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader)
                                .session(mockHttpSession))
                .andExpect(status().isOk());
        mockMvc.perform(get("/category/get-category-by-id/1"))
                .andExpect(status().is4xxClientError());
    }
}
