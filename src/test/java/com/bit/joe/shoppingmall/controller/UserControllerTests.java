package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTests {

    static final MySQLContainer<?> mysql = MySQLContainerConfig.getInstance();

    private MockMvc mockMvc;

    @Mock private UserService userService;

    @Mock private HttpSession session;

    @InjectMocks private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @Order(1)
    public void registerAdminUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("admin@example.com");
        userDto.setName("admin");
        userDto.setPassword("admin");
        userDto.setRole(UserRole.ADMIN);
        userDto.setGender(UserGender.MALE);
        userDto.setBirth("2021-01-01");

        ObjectMapper objectMapper = new ObjectMapper();
        String userDtoJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    public void getAllUsers() throws Exception {

        MockHttpSession mockHttpSession = new MockHttpSession();

        String basicAuthHeader =
                "Basic " + Base64.getEncoder().encodeToString("admin:admin".getBytes());

        mockMvc.perform(
                        get("/user/get-all")
                                .header("Authorization", basicAuthHeader)
                                .session(mockHttpSession))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void registerUserWithEmptyBody() throws Exception {
        UserDto userDto = new UserDto();

        ObjectMapper objectMapper = new ObjectMapper();
        String userDtoJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson))
                .andExpect(status().isBadRequest());
    }
}
