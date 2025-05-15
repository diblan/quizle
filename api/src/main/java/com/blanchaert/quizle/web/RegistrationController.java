package com.blanchaert.quizle.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegistrationController {

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // maps to templates/register.html
    }
}
