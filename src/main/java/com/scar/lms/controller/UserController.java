package com.scar.lms.controller;

import com.scar.lms.entity.User;
import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.scar.lms.entity.Role.ADMIN;
import static com.scar.lms.entity.Role.USER;

@Controller
@RequestMapping
public class UserController {

    private static final Long DEFAULT_USER_POINT = 0L;

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(final UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        System.out.println("Please sign up");
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (!authenticationService.validateRegistration(user.getUsername(), user.getPassword(),
                user.getDisplayName(), user.getEmail())) {
            model.addAttribute("error", "Invalid registration details.");
            return "register";
        }
        user.setPassword(authenticationService.encryptPassword(user.getPassword()));
        user.setPoints(DEFAULT_USER_POINT);
        user.setRole(ADMIN);
        userService.createUser(user);
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, Model model) {
        if (!authenticationService.validateAuthentication(username, password)) {
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "home";
    }
}
