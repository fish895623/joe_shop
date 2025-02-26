package com.bit.joe.shoppingmall.service.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bit.joe.shoppingmall.dto.ProductDto;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.entity.Category;
import com.bit.joe.shoppingmall.entity.Product;
import com.bit.joe.shoppingmall.exception.NotFoundException;
import com.bit.joe.shoppingmall.mapper.ProductMapper;
import com.bit.joe.shoppingmall.repository.CategoryRepository;
import com.bit.joe.shoppingmall.repository.ProductRepository;
import com.bit.joe.shoppingmall.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Response createProduct(
            Long categoryId, String image, String name, int quantity, int price) {
        Category category =
                categoryRepository
                        .findById(categoryId)
                        .orElseThrow(() -> new NotFoundException("Category not found"));

        Product product = new Product();
        product.setCategory(category);
        product.setPrice(price);
        product.setName(name);
        product.setQuantity(quantity);
        product.setImageURL(image);

        productRepository.save(product);
        return Response.builder().status(200).message("Product created successfully").build();
    }

    @Override
    public Response updateProduct(
            Long productId, Long categoryId, String image, String name, int quantity, int price) {
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new NotFoundException("Product not found"));

        Category category = null;
        if (categoryId != null) {
            category =
                    categoryRepository
                            .findById(categoryId)
                            .orElseThrow(() -> new NotFoundException("Category not found"));
        }

        if (category != null) product.setCategory(category);
        if (name != null) product.setName(name);
        if (price >= 0) product.setPrice(price);
        if (quantity >= 0) product.setQuantity(quantity);
        if (image != null) product.setImageURL(image);

        productRepository.save(product);
        return Response.builder().status(200).message("Product updated successfully").build();
    }

    @Override
    public Response deleteProduct(Long productId) {
        Product product =
                productRepository
                        .findById((productId))
                        .orElseThrow(() -> new NotFoundException("Product not found"));
        productRepository.delete(product);

        return Response.builder().status(200).message("Product deleted successfully").build();
    }

    @Override
    public Response getProductById(Long productId) {
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new NotFoundException("Product not found"));
        ProductDto productDto = ProductMapper.productToDto(product);

        return Response.builder()
                .status(200)
                .message("Product by Id retrieved successfully")
                .product(productDto)
                .build();
    }

    @Override
    public Response getAllProducts() {
        List<ProductDto> productList =
                productRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                        .map(ProductMapper::productToDto)
                        .collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .message("All products retrieved successfully")
                .productList(productList)
                .build();
    }

    @Override
    public Response getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        if (products.isEmpty()) {
            throw new NotFoundException("No Products found for this category");
        }
        List<ProductDto> productDtoList =
                products.stream().map(ProductMapper::productToDto).collect(Collectors.toList());

        return Response.builder()
                .status(200)
                .message("Products by category retrieved successfully")
                .productList(productDtoList)
                .build();
    }
}
