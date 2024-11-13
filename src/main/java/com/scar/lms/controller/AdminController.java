package com.scar.lms.controller;

import com.scar.lms.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/", ""})
    public String showAdminPage() {
        return "admin";
    }

    @GetMapping("/users")
    public String listAllUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "userList";
    }

    @PostMapping("/delete/user/{userId}")
    public String deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        return "redirect:/admin/users";
    }
}
