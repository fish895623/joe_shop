package com.bit.joe.shoppingmall.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bit.joe.shoppingmall.dto.UserDto;
import com.bit.joe.shoppingmall.entity.User;
import com.bit.joe.shoppingmall.enums.UserGender;
import com.bit.joe.shoppingmall.enums.UserRole;
import com.bit.joe.shoppingmall.repository.UserRepository;
import com.bit.joe.shoppingmall.service.Impl.UserServiceImpl;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

@ExtendWith({SpringExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@Testcontainers(parallel = true)
public class UserServiceTests {
    @Container
    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:lts");
    @Autowired private UserServiceImpl userService;
    @Autowired private UserRepository userRepository;

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
    @Transactional
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createUser() throws Exception {
        // Create a user admin and user
        UserDto adminDto = new UserDto();
        adminDto.setId(1L);
        adminDto.setEmail("admin@admin.com");
        adminDto.setPassword("admin");
        adminDto.setRole(UserRole.ADMIN);
        adminDto.setGender(UserGender.MALE);

        userService.createUser(adminDto);

        UserDto userDto = new UserDto();
        adminDto.setId(2L);
        adminDto.setEmail("user@admin.com");
        adminDto.setPassword("user");
        adminDto.setRole(UserRole.USER);
        adminDto.setGender(UserGender.MALE);

        userService.createUser(userDto);


        // Check if the user is created
        Optional<User> admin = userRepository.findById(1L);
        Optional<User> user = userRepository.findById(2L);

        // Assertions
        assertTrue(admin.isPresent());
        assertEquals("admin@admin.com", admin.get().getEmail());

        assertTrue(user.isPresent());
        assertEquals("user@admin.com", user.get().getEmail());
    }

    @Test
    void updateUser() {}

    @Test
    void deleteUser() {}

    @Test
    void getUserById() {}

    @Test
    void getUserByEmail() {}

    @Test
    void getAllUsers() {}

    @Test
    void login() {}

    @Test
    void logout() {}
}
