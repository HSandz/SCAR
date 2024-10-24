package com.scar.lms.controller;

import com.scar.lms.entity.User;
import com.scar.lms.service.UserServiceImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SignUpController {
    private final UserServiceImplement userService;

    @Autowired
    public SignUpController(UserServiceImplement userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        return "signup_page.html";
    }

    @PostMapping("/signup")
    public String processSignupForm(@ModelAttribute User user) {
        if (userService.userExists(user.getUsername(), user.getEmail())) {
            return "redirect:signup_page.html?error";
        }
        // Save the user to the database
        userService.saveUser(user);
        return "redirect:signup_success.html";
    }
}
