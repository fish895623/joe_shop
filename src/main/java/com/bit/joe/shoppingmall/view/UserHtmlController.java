package com.bit.joe.shoppingmall.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserHtmlController {

    @GetMapping("/main")
    public String Main() {
        return "user/mainPage";
    }

    @GetMapping("/login")
    public String Login() {
        return "login";
    }

    @GetMapping("/register")
    public String SignUp() {
        return "user/signUp";
    }

    @GetMapping("/my-page")
    public String MyPage() {
        return "user/myPage";
    }
}
