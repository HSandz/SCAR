package com.scar.lms.controller;

import com.scar.lms.entity.User;
import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.BookService;
import com.scar.lms.service.BorrowService;
import com.scar.lms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.scar.lms.entity.Role.ADMIN;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final BookService bookService;
    private final BorrowService borrowService;

    public AdminController(final UserService userService,
                           final AuthenticationService authenticationService,
                           final BookService bookService, BorrowService borrowService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.bookService = bookService;
        this.borrowService = borrowService;
    }

    @GetMapping("/users")
    public String listAllUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "users-list";
    }

    @GetMapping("/user/{userId}")
    public String showUserPage(@PathVariable int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "error/404";
        }
        model.addAttribute("user", user);
        return "user-view";
    }

    @GetMapping("/user/{userId}/edit")
    public String showUpdateUserForm(@PathVariable int userId, Model model) {
        User user = userService.findUserById(userId);
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PostMapping("/user/update")
    public String updateUser(@Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            return "user-edit";
        }
        userService.updateUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/user/{userId}/delete")
    public String deleteUser(@PathVariable int userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user.");
        }
        return "redirect:/admin/users";
    }


    @GetMapping("/user/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user-create";
    }

    @PostMapping("/user/create")
    public String createUser(@Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            return "user-create";
        }
        userService.createUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/user/{userId}/grantAuthority")
    @PreAuthorize("hasRole('ADMIN')")
    public String grantAuthority(@PathVariable int userId, RedirectAttributes redirectAttributes) {
        User user = userService.findUserById(userId);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/admin/users";
        }
        try {
            user.setRole(ADMIN);
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Authority granted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to grant authority.");
        }
        return "redirect:/admin/users";
    }

    @GetMapping({"", "/"})
    public String showAdminProfile(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        User user = getAuthenticatedUser(authentication);

        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "error/404";
        }

        model.addAttribute("admin", user);
        model.addAttribute("adminCount", userService.findUsersByRole(ADMIN).size());
        model.addAttribute("userCount", userService.findAllUsers().size());
        model.addAttribute("bookCount", bookService.findAllBooks().size());
        model.addAttribute("borrowCount", borrowService.findAllBorrows().size());

        for (int i = 1; i <= 12; i++) {
            model.addAttribute("borrowCountMonth" + i, borrowService.findBorrowsByMonth(i).size());
        }

        return "admin-profile";
    }

    @PostMapping("/profile/edit")
    public String showEditAdminProfileForm(Authentication authentication, Model model) {
        User user = getAuthenticatedUser(authentication);
        model.addAttribute("user", user);
        return "admin-profile";
    }

    private User getAuthenticatedUser(Authentication authentication) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        return userService.findUserByUsername(username);
    }

}
