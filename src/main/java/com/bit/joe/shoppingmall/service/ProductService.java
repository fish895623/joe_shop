package com.bit.joe.shoppingmall.service;

import com.bit.joe.shoppingmall.dto.response.Response;

public interface ProductService {

    Response createProduct(Long categoryId, String image, String name, int quantity, int price);

    Response updateProduct(
            Long productId, Long categoryId, String image, String name, int quantity, int price);

    Response deleteProduct(Long productId);

    Response getProductById(Long productId);

    Response getAllProducts();

    Response getProductsByCategory(Long categoryId);

    boolean existsByCategoryIdAndName(Long categoryId, String name);
}
