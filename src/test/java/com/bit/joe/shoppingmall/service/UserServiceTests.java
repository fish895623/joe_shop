package com.bit.joe.shoppingmall.service;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bit.joe.shoppingmall.repository.UserRepository;
import com.bit.joe.shoppingmall.service.Impl.UserServiceImpl;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTests {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;
    @InjectMocks private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Order(1)
    void createUser() throws Exception {}

    @Test
    void updateUser() {}

    @Test
    void deleteUser() {}

    @Test
    @Order(2)
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
