package com.scar.lms.controller;

import com.scar.lms.entity.User;
import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        if (!authenticationService.validateRegistration(user.getUsername(), user.getPassword(),
                user.getDisplayName(), user.getEmail())) {
            model.addAttribute("error", "Invalid registration details.");
            return "register";
        }
        user.setPassword(authenticationService.encryptPassword(user.getPassword()));
        user.setPoints(DEFAULT_USER_POINT);
        user.setRole(USER);
        userService.createUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "home";
    }

    @GetMapping("/admin")
    public String showAdminPage() {
        return "admin";
    }

    @GetMapping("/updatePassword")
    public String showUpdatePasswordForm(Model model) {
        model.addAttribute("user", new User());
        return "updatePassword";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            Model model) {

        String username = userDetails.getUsername();

        if (!authenticationService.updatePassword(username, oldPassword, newPassword)) {
            model.addAttribute("error", "Password update failed. Please check your old password and try again.");
            return "updatePassword";
        }
        model.addAttribute("success", "Password updated successfully.");
        return "login";
    }

}
