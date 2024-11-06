package com.scar.lms.controller;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.User;
import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

import static com.scar.lms.entity.Role.USER;

@Controller
@RequestMapping
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(final UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("username") final String username,
                               @RequestParam("password") final String password,
                               @RequestParam("displayName") final String displayName,
                               @RequestParam("email") final String email) {
        if (authenticationService.validateRegistration(username, password, displayName, email)) {
            userService.createUser(new User(username, password, displayName, email, USER, 0L));
            System.out.println("Created user");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username or password");
        }
        return "redirect:/home";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
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
