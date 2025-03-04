package com.bit.joe.shoppingmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bit.joe.shoppingmall.dto.ProductDto;
import com.bit.joe.shoppingmall.dto.response.Response;
import com.bit.joe.shoppingmall.service.ProductService;

@Controller
@RequestMapping("product")
public class ProductDetailHtmlController {

    private final ProductService productService;

    public ProductDetailHtmlController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product-detail/{productId}")
    public String getProductById(@PathVariable Long productId, Model model) {
        System.out.println("요청된 상품 ID: " + productId);

        // 상품 정보를 가져옵니다.
        Response response = productService.getProductById(productId);
        ProductDto product = response.getProduct();

        System.out.println("상품 이름: " + product.getName());
        System.out.println("상품 이미지 URL: " + product.getImageUrl());
        System.out.println("상품 가격: " + product.getPrice());

        // 상품 정보를 모델에 추가하여 템플릿에서 사용 가능하게 함
        model.addAttribute("product", response.getProduct());

        // "product-detail" 템플릿을 반환
        return "thymeleaf/product-detail";
    }
}
