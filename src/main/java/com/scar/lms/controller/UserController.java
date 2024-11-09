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

    @GetMapping("/admin/users")
    public String listAllUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "userList";
    }


    @GetMapping("/profile")
    public String showProfilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findUsersByUsername(userDetails.getUsername());
        model.addAttribute("user", userDetails);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findUsersByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "editProfile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute("user") User updatedUser,
                                Model model) {
        User user = userService.findUsersByUsername(userDetails.getUsername());
        user.setDisplayName(updatedUser.getDisplayName());
        user.setEmail(updatedUser.getEmail());
        userService.updateUser(user);
        model.addAttribute("success", "Profile updated successfully.");
        return "redirect:/profile";
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

    @GetMapping("/profile/delete")
    public String showDeleteAccountForm() {
        return "deleteAccount";
    }

    @PostMapping("/profile/delete")
    public String deleteAccount(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findUsersByUsername(userDetails.getUsername());
        userService.deleteUser(user.getId());
        model.addAttribute("success", "Account deleted successfully.");
        return "redirect:/logout"; // Log out the user after account deletion
    }

}
