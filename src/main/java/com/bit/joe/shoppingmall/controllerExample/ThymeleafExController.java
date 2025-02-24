package com.bit.joe.shoppingmall.controllerExample;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bit.joe.shoppingmall.entity.User;

@Controller
@RequestMapping("thymeleaf")
public class ThymeleafExController {

    @GetMapping("/ex1")
    public String thymeleaf(Model model) {
        model.addAttribute("pageTitle", "Using th:text example");
        model.addAttribute("data", "Hello, Thymeleaf!");

        return "thymeleaf/thymeleaf";
    }

    @GetMapping("/ex2")
    public String thymeleaf2(Model model) {
        List<User> users = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setName("sameple user" + i);
            user.setEmail(i + "@" + i);

            users.add(user);
        }

        model.addAttribute("pageTitle", "Using th:each example");
        model.addAttribute("users", users);

        return "thymeleaf/thymeleaf2";
    }
}
