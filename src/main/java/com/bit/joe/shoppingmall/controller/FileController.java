package com.bit.joe.shoppingmall.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/api/files")
public class FileController {
    private static final List<String> ALLOWED_EXTENSIONS =
            Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Value("${file.upload.directory:uploads/images}")
    private String uploadDir;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        // Validate file
        if (file.isEmpty()) {
            response.put("error", "Please select a file to upload");
            return ResponseEntity.badRequest().body(response);
        }

        // Check file type
        if (!ALLOWED_EXTENSIONS.contains(file.getContentType())) {
            response.put("error", "Only JPEG, PNG, GIF, and WebP files are allowed");
            return ResponseEntity.badRequest().body(response);
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            response.put("error", "File size exceeds the limit of 5MB");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename with original extension
            String originalFilename = file.getOriginalFilename();
            if (Objects.requireNonNull(originalFilename).isEmpty()) {
                response.put("error", "Invalid file name");
                return ResponseEntity.badRequest().body(response);
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID() + extension;

            // Save the file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            response.put("filename", filename);
            response.put("url", "/api/files/" + filename);
            response.put("message", "File uploaded successfully");

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<byte[]> getFile(@PathVariable String filename) {
        log.info("getFile: {}", filename);
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(fileContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
