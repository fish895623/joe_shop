package com.bit.joe.shoppingmall.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.microsoft.playwright.*;

// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExampleTests {
    static Playwright playwright;
    static Browser browser;
    Page page;
    @LocalServerPort private int port;

    @BeforeAll
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @BeforeEach
    void createPage() {
        page = browser.newPage();
        page.navigate("https://example.com");
    }

    @Test
    void testHomePageLoads() {
        assertTrue(page.title().contains("Example Domain")); // Replace with expected title
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
