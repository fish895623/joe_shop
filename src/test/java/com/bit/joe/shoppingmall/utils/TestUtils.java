package com.bit.joe.shoppingmall.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class TestUtils {
    private static final String SCREENSHOT_DIR = "build/screenshot";

    public static void screenShot(Page page, String fileName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedTime = LocalDateTime.now().format(formatter);

        Path screenshotPath =
                Paths.get(SCREENSHOT_DIR, String.format("%s-%s.png", fileName, formattedTime));

        try {
            Files.createDirectories(Paths.get(SCREENSHOT_DIR)); // Ensure directory exists

            page.locator("body")
                    .screenshot(new Locator.ScreenshotOptions().setPath(screenshotPath));

            cleanupOldScreenshots(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void cleanupOldScreenshots(String fileName) throws IOException {
        List<Path> files =
                Files.list(Paths.get(SCREENSHOT_DIR))
                        .filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().startsWith(fileName))
                        .sorted(Comparator.comparing(Path::toFile).reversed())
                        .toList();

        for (int i = 5; i < files.size(); i++) {
            Files.delete(files.get(i));
        }
    }
}
