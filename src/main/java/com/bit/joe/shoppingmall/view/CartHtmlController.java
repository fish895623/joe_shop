package com.bit.joe.shoppingmall.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
public class CartHtmlController {
    @GetMapping
    public String cart() {
        return "product/cart";
    }
}
