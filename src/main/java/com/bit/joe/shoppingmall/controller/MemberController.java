package com.bit.joe.shoppingmall.controller;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/member")
public class MemberController {
    @GetMapping
    public String index() {
        return "admin/member"; // Html 페이지로 이동
    }

    // JSON 데이터 반환하는 메서드
    @GetMapping("/data")
    @ResponseBody
    public List<Member> getMemberData() {
        List<Member> members = new ArrayList<>();

        return members;
    }
}
