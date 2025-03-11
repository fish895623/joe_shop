package com.bit.joe.shoppingmall.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartHtmlController {

    @GetMapping()
    public String getCartPage() {

        return "product/cart";
    }
}
