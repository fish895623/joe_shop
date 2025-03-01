package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;

import com.bit.joe.shoppingmall.config.MySQLContainerConfig;
import com.bit.joe.shoppingmall.service.CategoryService;

import jakarta.servlet.http.HttpSession;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryControllerTests {
    static final MySQLContainer<?> mysql = MySQLContainerConfig.getInstance();
    private MockMvc mockMvc;
    @Mock private CategoryService categoryService;
    @Mock private HttpSession session;
    @InjectMocks private CategoryController categoryController;

    @Test
    @Order(1)
    void testCreateCategory() throws Exception {
        mockMvc.perform(post("/category/create")).andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void testGetAllCategories() throws Exception {
        mockMvc.perform(get("/category/get-all")).andExpect(status().isOk());
    }

    @Test
    @Order(3)
    void testUpdateCategory() throws Exception {
        mockMvc.perform(post("/category/update/1")).andExpect(status().isOk());
    }

    @Test
    @Order(5)
    void testDeleteCategory() throws Exception {
        mockMvc.perform(post("/category/delete/1")).andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void testGetCategoryById() throws Exception {
        mockMvc.perform(get("/category/get-category-by-id/1")).andExpect(status().isOk());
    }
}
