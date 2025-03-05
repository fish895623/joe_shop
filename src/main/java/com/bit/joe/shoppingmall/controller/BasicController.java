package com.bit.joe.shoppingmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/basic")
public class BasicController {

    @GetMapping("/main")
    public String Main() {
        return "thymeleaf/customuserinterface";
    }

    @GetMapping("/login")
    public String Login() {
        return "thymeleaf/login";
    }

    @GetMapping("/signUp")
    public String SignUp() {
        return "thymeleaf/signUp";
    }

    @GetMapping("/myPage")
    public String MyPage() {
        return "thymeleaf/myPage";
    }

    @GetMapping("/updateProfile")
    public String Update() {
        return "thymeleaf/updateProfile";
    }
}
