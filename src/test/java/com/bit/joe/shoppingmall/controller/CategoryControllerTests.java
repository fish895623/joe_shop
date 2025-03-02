package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import com.bit.joe.shoppingmall.dto.CategoryDto;
import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryControllerTests {
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
    @Mock private CategoryService categoryService;
    @Mock private HttpSession session;
    @InjectMocks private CategoryController categoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    @Order(1)
    void testCreateCategory() throws Exception {

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
    }

    @Test
    @Order(2)
    void testGetAllCategories() throws Exception {
        mockMvc.perform(get("/category/get-all")).andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void testUpdateCategory() throws Exception {
        var categoryDto = CategoryDto.builder().categoryName("Update Category").build();

        ObjectMapper objectMapper = new ObjectMapper();
        String contentJson = objectMapper.writeValueAsString(categoryDto);

        mockMvc.perform(
                        put("/category/update/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader)
                                .content(contentJson)
                                .session(mockHttpSession))
                .andExpect(status().isOk());

        mockMvc.perform(get("/category/get-category-by-id/1")).andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void testGetCategoryById() throws Exception {
        mockMvc.perform(get("/category/get-category-by-id/1")).andExpect(status().isOk());
    }

    @Test
    @Order(5)
    void testDeleteCategory() throws Exception {
        mockMvc.perform(
                        delete("/category/delete/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader)
                                .session(mockHttpSession))
                .andExpect(status().isOk());
        mockMvc.perform(get("/category/get-category-by-id/1")).andExpect(status().isOk());
    }
}
