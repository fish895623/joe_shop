package com.bit.joe.shoppingmall.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bit.joe.shoppingmall.dto.ProductDto;
import com.bit.joe.shoppingmall.dto.request.ProductRequest;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.Product;
import com.bit.joe.shoppingmall.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

    @PutMapping("/update/{productId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateProduct(
            @PathVariable Long productId, @Valid @RequestBody ProductRequest productRequest) {
        // Check if the product name is unique within the same category, excluding the current
        // product
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

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String keyword) {
        try {
            List<Product> products = productService.searchProductsByKeyword(keyword);

            if (products.isEmpty()) {
                Response response =
                        Response.builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .message("검색 결과가 없습니다.")
                                .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            List<ProductDto> productDtos =
                    products.stream()
                            .map(
                                    product ->
                                            ProductDto.builder()
                                                    .id(product.getId())
                                                    .name(product.getName())
                                                    .price(product.getPrice())
                                                    .imageUrl(product.getImageURL())
                                                    .build())
                            .collect(Collectors.toList());

            Response response =
                    Response.builder()
                            .status(HttpStatus.OK.value())
                            .message("검색 결과")
                            .productList(productDtos)
                            .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Response response =
                    Response.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("internal server error!")
                            .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
