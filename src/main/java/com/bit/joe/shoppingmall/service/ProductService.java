package com.bit.joe.shoppingmall.service;

import java.util.List;

import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.Product;

public interface ProductService {

    Response createProduct(Long categoryId, String image, String name, int quantity, int price);

    Response updateProduct(
            Long productId, Long categoryId, String image, String name, int quantity, int price);

    Response deleteProduct(Long productId);

    Response getProductById(Long productId);

    Response getAllProducts();

    Response getProductsByCategory(Long categoryId);

    boolean existsByCategoryIdAndName(Long categoryId, String name);

    List<Product> searchProductsByKeyword(String keyword);
}
