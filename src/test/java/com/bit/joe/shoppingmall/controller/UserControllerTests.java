package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.bit.joe.shoppingmall.config.MySQLContainerConfig;
import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.mapper.UserMapper;
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
@ContextConfiguration(initializers = MySQLContainerConfig.class)
public class UserControllerTests {
    @Container public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:lts");
    UserDto userDto =
            UserDto.builder()
                    .name("admin")
                    .password("admin")
                    .email("admin@example.com")
                    .role(UserRole.ADMIN)
                    .gender(UserGender.MALE)
                    .birth("2021-01-01")
                    .active(true)
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

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService)).build();
    }

    @Test
    @Order(1)
    public void registerAdminUser() throws Exception {

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

    @Test
    @Order(4)
    public void loginUser() throws Exception {
        userRepository.save(UserMapper.toEntity(userDto));

        MockHttpSession mockHttpSession = new MockHttpSession();
        String basicAuthHeader =
                "Basic " + Base64.getEncoder().encodeToString("admin@example.com:admin".getBytes());

        ObjectMapper objectMapper = new ObjectMapper();
        String userDtoJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(
                        post("/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", basicAuthHeader)
                                .content(userDtoJson)
                                .session(mockHttpSession))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    public void updateUser() throws Exception {
        User user = userRepository.save(UserMapper.toEntity(userDto));

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", UserMapper.toDto(user));

        UserDto userDto = new UserDto();
        userDto.setEmail("admin@example.com");
        userDto.setName("admin");
        userDto.setPassword("admin");
        userDto.setRole(UserRole.ADMIN);
        userDto.setGender(UserGender.FEMALE);
        userDto.setBirth("2021-01-01");
        userDto.setActive(true);

        ObjectMapper objectMapper = new ObjectMapper();
        String userDtoJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(
                        put("/user/update/3")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson)
                                .header("Authorization", basicAuthHeader)
                                .session(mockHttpSession))
                .andExpect(status().isOk());
    }
}
