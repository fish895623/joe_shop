package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.jwt.JWTUtil;
import com.bit.joe.shoppingmall.mapper.UserMapper;
import com.bit.joe.shoppingmall.repository.*;
import com.bit.joe.shoppingmall.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
@Rollback
public class UserControllerTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;
    @Autowired JWTUtil jwtUtil;

    UserDto adminDto =
            UserDto.builder()
                    .name("admin")
                    .gender(UserGender.MALE)
                    .role(UserRole.ADMIN)
                    .birth("2021-01-01")
                    .email("admin for User Controller Test")
                    .password("admin")
                    .active(true)
                    .build();

    UserDto userDto =
            UserDto.builder()
                    .name("user")
                    .gender(UserGender.MALE)
                    .role(UserRole.USER)
                    .birth("2021-01-01")
                    .email("user for User Controller Test")
                    .password("user")
                    .active(true)
                    .build();
    String adminJwtToken, userJwtToken;
    Cookie adminCookie, userCookie;

    @BeforeEach
    public void setUp() {

        userService.createUser(adminDto);
        userService.createUser(userDto);

        adminJwtToken =
                jwtUtil.createJwt(adminDto.getEmail(), adminDto.getRole().name(), 36000000L);
        userJwtToken =
                jwtUtil.createJwt(userDto.getEmail(), userDto.getRole().name(), 36000000L);

        adminCookie = new Cookie("token", adminJwtToken);
        userCookie = new Cookie("token", userJwtToken);
    }

    @Test
    @DisplayName("Test form login and JWT cookie creation")
    public void testFormLoginAndJwtCookie() throws Exception {
        // Save the test user
        userService.createUser(adminDto);

        // Perform form-based login
        mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("username", adminDto.getEmail())
                                .param("password", adminDto.getPassword()))
                .andExpect(status().isOk())
                .andExpect(
                        result -> {
                            // Check if cookie exists
                            Cookie[] cookies = result.getResponse().getCookies();
                            Assertions.assertNotNull(cookies, "Response should have cookies");

                            // Find the token cookie
                            Cookie tokenCookie = null;
                            for (Cookie cookie : cookies) {
                                if ("token".equals(cookie.getName())) {
                                    tokenCookie = cookie;
                                    break;
                                }
                            }

                            // Verify cookie exists and has valid JWT token
                            Assertions.assertNotNull(tokenCookie, "Should have a token cookie");
                            String token = tokenCookie.getValue();
                            Assertions.assertFalse(token.isEmpty(), "Token should not be empty");

                            // Verify JWT properties
                            Assertions.assertFalse(
                                    jwtUtil.isExpired(token), "Token should not be expired");
                            Assertions.assertEquals(
                                    adminDto.getEmail(),
                                    jwtUtil.getUsername(token),
                                    "Token should contain " + "correct username");
                            Assertions.assertEquals(
                                    adminDto.getRole().name(),
                                    jwtUtil.getRole(token),
                                    "Token should contain " + "correct role");
                        });
    }

    @Test
    @DisplayName("Register admin user")
    public void registerAdminUser() throws Exception {
        // prepare data
        String userDtoJson = new ObjectMapper().writeValueAsString(adminDto);

        // test
        mockMvc.perform(
                        post("/api/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Get all users")
    public void getAllUsers() throws Exception {
        userService.createUser(adminDto);

        MockHttpSession mockHttpSession = new MockHttpSession();

        mockMvc.perform(
                        get("/api/user/get-all")
                            .cookie(adminCookie))
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/api/user/get-all")
                            .cookie(userCookie))
                .andExpect(
                        result -> {
                            var status = result.getResponse().getStatus();
                            Assertions.assertTrue(
                                    status >= 400 && status < 500, "User cannot get all users");
                        });
    }

    @Test
    @DisplayName("Register user with empty body")
    public void registerUserWithEmptyBody() throws Exception {
        UserDto userDto = new UserDto();

        ObjectMapper objectMapper = new ObjectMapper();
        String userDtoJson = objectMapper.writeValueAsString(userDto);

        mockMvc.perform(
                        post("/api/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update user")
    public void updateUser() throws Exception {

        // Create a UserDto from the saved user entity and modify it
        userDto.setEmail("updated email for user controller test");

        // Convert the UserDto to JSON
        String userDtoJson = new ObjectMapper().writeValueAsString(userDto);

        // Perform the PUT request to update the user
        mockMvc.perform(
                        put("/api/user/update/" + this.userDto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson)
            .cookie(userCookie))
                .andExpect(status().isOk());
    }

    // 유저 탈퇴 테스트
    @Test
    @DisplayName("Withdraw user")
    public void withdrawUser() throws Exception {


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        var insertData = userService.getUserByEmail(userDto.getEmail()).getUser();
        String userDtoJson;

        try {
            userDtoJson = objectMapper.writeValueAsString(insertData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert user data to JSON", e);
        }

        mockMvc.perform(
                        get("/api/user/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                            .cookie(userCookie)
                                .content(userDtoJson))
                .andExpect(status().isOk());
    }
}
