package com.bit.joe.shoppingmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
public class ProductdetailController {
    @GetMapping
    public String productdetail() {
        return "thymeleaf/product/product-detail";
    }
}
