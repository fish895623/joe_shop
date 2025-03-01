package com.bit.joe.shoppingmall.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bit.joe.shoppingmall.dto.request.ProductRequest;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.exception.InvalidCredentialsException;
import com.bit.joe.shoppingmall.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> createProduct(@RequestBody ProductRequest productRequest) {
        // Validate the fields in the productRequest
        if (productRequest.getCategoryId() == null
                || productRequest.getImage().isEmpty()
                || productRequest.getName().isEmpty()
                || productRequest.getQuantity() <= 0
                || productRequest.getPrice() <= 0) {
            throw new InvalidCredentialsException("All Fields are Required");
        }

        return ResponseEntity.ok(
                productService.createProduct(
                        productRequest.getCategoryId(),
                        productRequest.getImage(),
                        productRequest.getName(),
                        productRequest.getQuantity(),
                        productRequest.getPrice()));
    }

    @PutMapping("/update/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateProduct(
            @PathVariable Long productId, @RequestBody ProductRequest productRequest) {
        if (productRequest.getQuantity() <= 0 && productRequest.getPrice() <= 0) {
            throw new InvalidCredentialsException("Quantity and price must be greater than 0.");
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
    public ResponseEntity<Response> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/get-all")
    public ResponseEntity<Response> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/get-by-category-id/{categoryId}")
    public ResponseEntity<Response> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }
}
