package com.scar.lms.controller;

import com.scar.lms.entity.User;
import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.BookService;
import com.scar.lms.service.BorrowService;
import com.scar.lms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        model.addAttribute("user", user);
        return "user-view";
    }

    @GetMapping("/edit/user/{userId}")
    public String showUpdateUserForm(@PathVariable int userId, Model model) {
        User user = userService.findUserById(userId);
        model.addAttribute("user", user);
        return "user-edit";
    }

    @PostMapping("/user/update")
    public String updateUser(User user) {
        userService.updateUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/user/{userId}")
    public String deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        return "redirect:/admin/users";
    }

    @GetMapping("/user/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        return "user-create";
    }

    @PostMapping("/user/create")
    public String createUser(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user-create";
        }
        userService.createUser(user);
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

    @GetMapping({"", "/"})
    public String showAdminProfile(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUserByUsername(username);

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
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUserByUsername(username);
        model.addAttribute("user", user);
        return "admin-profile";
    }
}
