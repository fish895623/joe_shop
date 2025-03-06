package com.bit.joe.shoppingmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/inventory")
public class InventoryController {
    @GetMapping
    public String index() {
        return "admin/inventory";
    }
}
