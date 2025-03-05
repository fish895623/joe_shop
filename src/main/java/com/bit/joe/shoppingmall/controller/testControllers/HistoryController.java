package com.bit.joe.shoppingmall.controller.testControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/history")
public class HistoryController {
    @GetMapping
    public String history() {
        return "thymeleaf/product/history";
    }
}
