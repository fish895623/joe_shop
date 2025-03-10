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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

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

@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
@Rollback
public class CategoryControllerTests {
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
    @Autowired private UserServiceImpl userService;
    @Autowired private CategoryServiceImpl categoryService;
    @Autowired private HttpSession session;
    @Autowired private CategoryController categoryController;
    @Autowired private ProductController productController;
    @PersistenceContext private EntityManager entityManager;
    @Autowired private ProductServiceImpl productService;
    @Autowired private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        userService.createUser(userDto);
        userService.createUser(adminDto);
    }

    @Test
    void createCategory() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().categoryName("Test Category").build();

        String contentJson = new ObjectMapper().writeValueAsString(categoryDto);

        mockMvc.perform(
                        post("/category/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", adminBasicAuth)
                                .content(contentJson)
                                .session(mockHttpSession))
                .andExpect(status().isOk());
        var repo = categoryRepository.findAll();
        Assertions.assertEquals(1, repo.size());
    }

    @Test
    void getCategories() throws Exception {
        // Get All Categories
        mockMvc.perform(get("/category/get-all")).andExpect(status().isOk());

        // Get Single Category
        Category category = new Category();
        category.setCategoryName("Test Category");
        Long savedCategoryId =
                categoryService
                        .createCategory(CategoryMapper.categoryToDto(category))
                        .getCategory()
                        .getId();

        mockMvc.perform(get("/category/get-category-by-id/" + savedCategoryId))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategory() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("Test Category");

        String contentJson = new ObjectMapper().writeValueAsString(categoryDto);

        mockMvc.perform(
                        post("/category/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", adminBasicAuth)
                                .content(contentJson)
                                .session(mockHttpSession))
                .andExpect(status().isOk());

        var createdCategory = categoryRepository.findAll();
        Assertions.assertEquals(1, createdCategory.size(), "카테고리가 정상적으로 생성되어야 합니다.");
    }

    @Test
    void roleUserDeleteCategory() throws Exception {
        // insert Data
        String insertData;

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName("Test Category");

        insertData = new ObjectMapper().writeValueAsString(categoryDto);
        mockMvc.perform(
                        post("/category/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", adminBasicAuth)
                                .content(insertData)
                                .session(mockHttpSession))
                .andExpect(status().isOk());

        // get created category especially id
        Category createdCategory =
                categoryRepository.findByCategoryName(categoryDto.getCategoryName()).orElse(null);
        assert createdCategory != null; // createdCategory must not be null
        Long createdCategoryId = createdCategory.getId();

        // delete data
        mockMvc.perform(
                        delete("/category/delete/" + createdCategoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", adminBasicAuth)
                                .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(
                        result -> {
                            if (result.getResolvedException() != null) {
                                throw new AssertionError(
                                        "Category delete by id failed: "
                                                + result.getResolvedException().getMessage());
                            }
                        });
    }

    @Test
    void updateNonExistCategory() {}
}
