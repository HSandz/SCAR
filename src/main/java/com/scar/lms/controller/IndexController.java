package com.scar.lms.controller;

import com.scar.lms.entity.User;
import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.scar.lms.entity.Role.USER;

@SuppressWarnings("SameReturnValue")
@Controller
@RequestMapping
public class IndexController {

    static final Long DEFAULT_USER_POINT = 0L;

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public IndexController(final UserService userService,
                           final AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @GetMapping({"/", ""})
    public String defaultHome() {
        return "redirect:/home";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (!authenticationService.validateUsername(user.getUsername())) {
            model.addAttribute("error",
                    "Username is invalid or taken. Username must be between 6 and 20 characters.");
            return "register";
        }
        if (!authenticationService.validatePassword(user.getPassword())) {
            model.addAttribute("error",
                    "Password is invalid. Password must be between 8 and 20 characters.");
            return "register";
        }
        if (!authenticationService.validateEmail(user.getEmail())) {
            model.addAttribute("error", "Email is invalid or taken.");
            return "register";
        }
        if (!authenticationService.validateDisplayName(user.getDisplayName())) {
            model.addAttribute("error",
                    "Display name is invalid. Length must be between 3 and 20 characters.");
            return "register";
        }
        if (!authenticationService.validateRegistration(user.getUsername(), user.getPassword(),
                user.getDisplayName(), user.getEmail())) {
            model.addAttribute("error", "Registration details are invalid or taken.");
            return "register";
        }
        extractedRegisterUser(user);
        return "redirect:/login";
    }

    private void extractedRegisterUser(User user) {
        user.setPassword(authenticationService.encryptPassword(user.getPassword()));
        user.setPoints(DEFAULT_USER_POINT);
        user.setRole(USER);
        userService.createUser(user);
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "home";
    }
}
