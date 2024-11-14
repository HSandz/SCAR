package com.scar.lms.controller;

import com.scar.lms.entity.User;
import com.scar.lms.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.scar.lms.entity.Role.ADMIN;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
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

    @GetMapping("/user/{userId}")
    public String showUserPage(@PathVariable int userId, Model model) {
        User user = userService.findUserById(userId);
        model.addAttribute("user", user);
        return "user-view";
    }

    @PostMapping("/delete/user/{userId}")
    public String deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        return "redirect:/admin/users";
    }

    @PostMapping("/grantAuthority/user/{userId}")
    public String grantAuthority(@PathVariable int userId, Model model) {
        User user = userService.findUserById(userId);
        model.addAttribute("user", user);
        user.setRole(ADMIN);
        userService.updateUser(user);
        return "redirect:/admin/user";
    }
}
