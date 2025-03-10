package com.bit.joe.shoppingmall.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/insight")
    public String insight() {
        return "admin/insight";
    }

    @GetMapping("/inventory")
    public String inventory() {
        return "admin/inventory";
    }

    @GetMapping("/member")
    public String member() {
        return "admin/member";
    }
}
