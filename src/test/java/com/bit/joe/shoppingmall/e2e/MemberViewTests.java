package com.bit.joe.shoppingmall.e2e;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import com.bit.joe.shoppingmall.utils.TestUtils;
import com.microsoft.playwright.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource("classpath:application-test.properties")
public class MemberViewTests {
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
    }

    @Test
    void testHomePageLoads() {
        page.navigate(LOCALHOST + "admin/member");
        TestUtils.screenShot(page, "root_page");
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
