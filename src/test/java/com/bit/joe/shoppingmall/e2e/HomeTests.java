package com.bit.joe.shoppingmall.e2e;


import com.bit.joe.shoppingmall.utils.TestUtils;
import com.microsoft.playwright.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource("classpath:application-test.properties")
public class HomeTests {
    static Playwright playwright;
    static Browser browser;
    Page page;
    private String LOCALHOST;
    @LocalServerPort private int port;

    @BeforeAll
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        LOCALHOST = "http://127.0.0.1:" + port + "/";
    }

    @BeforeEach
    void createPage() {
        page = browser.newPage();
        page.navigate(LOCALHOST);
    }

    @Test
    void testHomePageLoads() throws InterruptedException {
        TestUtils.screenShot(page, "root_page");
        assertTrue(page.title().contains("Home Page"));
    }

    @AfterEach
    void closePage() {
        page.close();
    }

    @AfterAll
    void teardown() {
        browser.close();
        playwright.close();
    }
}
