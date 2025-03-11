package com.bit.joe.shoppingmall.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartHtmlController {

    @GetMapping("/{userId}")
    public String getCartPage(@PathVariable Long userId, Model model) {
        model.addAttribute("userId", userId); // 사용자 ID를 모델에 추가하여 페이지에서 사용
        return "product/cart"; // product/cart.html 페이지를 반환
    }
}
