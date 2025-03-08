package com.bit.joe.shoppingmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/user")
public class UserHtmlController {

    @GetMapping("/main")
    public String Main() {
        return "user/mainPage";
    }

    @GetMapping("/login")
    public String Login() {
        return "user/login";
    }

    @GetMapping("/signUp")
    public String SignUp() {
        return "user/signUp";
    }

    @GetMapping("/myPage")
    public String MyPage() {
        return "user/myPage";
    }
}
