package com.scar.lms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/login")
    public String login() {
        return "login";  // return the login.html template
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    @GetMapping("/signup")
    public String signUp() {
        return "signup";  // return the signup.html template
    }

    @GetMapping("/home")
    public String home() {
        return "home";  // after successful login
    }
}
