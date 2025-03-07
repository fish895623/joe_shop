package com.bit.joe.shoppingmall.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.mapper.UserMapper;
import com.bit.joe.shoppingmall.repository.*;
import com.bit.joe.shoppingmall.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

@TestMethodOrder(MethodOrderer.Random.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
@Transactional
public class UserControllerTests {
    User adminEntity, userEntity;
    String adminBasicAuth, userBasicAuth;
    MockHttpSession mockHttpSession = new MockHttpSession();
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;

    @BeforeEach
    public void setUp() {
        adminEntity =
            User.builder()
                .name("admin")
                .gender(UserGender.MALE)
                .role(UserRole.ADMIN)
                .birth("2021-01-01")
                .email("admin@example.com")
                .password("admin")
                .active(true)
                .build();
        userEntity =
            User.builder()
                .name("user")
                .password("user")
                .email("user@example.com")
                .role(UserRole.USER)
                .gender(UserGender.MALE)
                .birth("2021-01-01")
                .active(true)
                .build();
        adminBasicAuth =
            "Basic "
                + Base64.getEncoder()
                .encodeToString(
                    (adminEntity.getEmail() + ":" + adminEntity.getPassword())
                        .getBytes());
        userBasicAuth =
            "Basic "
                + Base64.getEncoder()
                .encodeToString(
                    (userEntity.getEmail() + ":" + userEntity.getPassword())
                        .getBytes());
    }

    @Test
    @DisplayName("Register admin user")
    public void registerAdminUser() throws Exception {
        // prepare data
        String userDtoJson = new ObjectMapper().writeValueAsString(UserMapper.toDto(adminEntity));

        // test
        mockMvc.perform(
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Get all users")
    public void getAllUsers() throws Exception {
        userService.createUser(UserMapper.toDto(adminEntity));

        MockHttpSession mockHttpSession = new MockHttpSession();

        mockMvc.perform(
                        get("/user/get-all")
                                .header("Authorization", adminBasicAuth)
                                .session(mockHttpSession))
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/user/get-all")
                                .header("Authorization", userBasicAuth)
                                .session(mockHttpSession))
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
                        post("/user/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login user")
    public void loginUser() throws Exception {
        userService.createUser(UserMapper.toDto(userEntity));

        MockHttpSession mockHttpSession = new MockHttpSession();

        ObjectMapper objectMapper = new ObjectMapper();
        String userDtoJson = objectMapper.writeValueAsString(UserMapper.toDto(userEntity));

        mockMvc.perform(
                        post("/user/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", userBasicAuth)
                                .content(userDtoJson)
                                .session(mockHttpSession))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Update user")
    public void updateUser() throws Exception {
        // Save the user entity to the repository
        userEntity = userRepository.save(userEntity);

        // Create a mock HTTP session and set the user attribute
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", UserMapper.toDto(userEntity));

        // Create a UserDto from the saved user entity and modify it
        UserDto userDto = UserMapper.toDto(userEntity);
        userDto.setActive(false);

        // Convert the UserDto to JSON
        String userDtoJson = new ObjectMapper().writeValueAsString(userDto);

        // Perform the PUT request to update the user
        mockMvc.perform(
                put("/user/update/" + userEntity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(userDtoJson)
                    .header("Authorization", userBasicAuth)
                    .session(mockHttpSession))
            .andExpect(status().isOk());
    }

    // 유저 탈퇴 테스트
    @Test
    @DisplayName("Withdraw user")
    public void withdrawUser() throws Exception {
        userRepository.save(userEntity);

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", UserMapper.toDto(userEntity));

        String userDtoJson = new ObjectMapper().writeValueAsString(UserMapper.toDto(userEntity));

        mockMvc.perform(
                        get("/user/withdraw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userDtoJson)
                                .header("Authorization", userBasicAuth)
                                .session(mockHttpSession))
                .andExpect(status().isOk());
    }
}
