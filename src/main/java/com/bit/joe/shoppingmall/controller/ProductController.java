package com.bit.joe.shoppingmall.controller;

import com.bit.joe.shoppingmall.dto.request.ProductRequest;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.service.ProductService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> createProduct(
            @Valid @RequestBody ProductRequest productRequest) {

        boolean productExistsInCategory =
                productService.existsByCategoryIdAndName(
                        productRequest.getCategoryId(), productRequest.getName());

        if (productExistsInCategory) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            Response.builder()
                                    .status(400)
                                    .message(
                                            "Product name must be unique within the same category.")
                                    .build());
        }
        // If everything is fine, proceed with product creation
        return ResponseEntity.ok(
                productService.createProduct(
                        productRequest.getCategoryId(),
                        productRequest.getImage(),
                        productRequest.getName(),
                        productRequest.getQuantity(),
                        productRequest.getPrice()));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateProduct(
            @Valid @RequestBody ProductRequest productRequest) {
        // Check if the product name is unique within the same category, excluding the current
        // product
        Long productId = productRequest.getProductId();

        boolean productExistsInCategory =
                productService.existsByCategoryIdAndName(
                        productRequest.getCategoryId(), productRequest.getName());

        if (productExistsInCategory && !productRequest.getName().equals(productRequest.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            Response.builder()
                                    .status(400)
                                    .message(
                                            "Product name must be unique within the same category.")
                                    .build());
        }
        return ResponseEntity.ok(
                productService.updateProduct(
                        productId,
                        productRequest.getCategoryId(),
                        productRequest.getImage(),
                        productRequest.getName(),
                        productRequest.getQuantity(),
                        productRequest.getPrice()));
    }

    @DeleteMapping("/delete/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.deleteProduct(productId));
    }

    @GetMapping("/get-by-product-id/{productId}")
    public ResponseEntity<Response> getProductById(@PathVariable("productId") Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/get-all")
    public ResponseEntity<Response> getAllProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") long size) {
        if (page == -1) {
            log.info("Fetching all products without pagination");
            return ResponseEntity.ok(productService.getAllProducts());
        }
        return ResponseEntity.ok(productService.getAllProducts(page, size));
    }

    @GetMapping("/get-by-category-id/{categoryId}")
    public ResponseEntity<Response> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") long size) {

        Response response = productService.searchProductsByKeyword(keyword, page, size);

        return ResponseEntity.ok(response);
    }
}
