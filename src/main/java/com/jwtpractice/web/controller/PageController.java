package com.jwtpractice.web.controller;

import com.jwtpractice.handler.aop.annotation.Authorization;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/main")
    public String loadMainPage() {
        return "main";
    }

    @GetMapping("/signup")
    public String loadSignUpPage() {
        return "sign_up";
    }

    @GetMapping("/user")
    public String loadUserPage() {
        return "user/user";
    }

    @GetMapping("/admin")
    public String loadAdminPage() {
        return "admin/admin";
    }
}