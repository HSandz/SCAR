package com.scar.lms.controller;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.User;
import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.BookService;
import com.scar.lms.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static com.scar.lms.entity.Role.USER;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Long DEFAULT_USER_POINT = 0L;

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final BookService bookService;

    @Autowired
    public UserController(final UserService userService, AuthenticationService authenticationService, BookService bookService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.bookService = bookService;
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
        return "redirect:/logout";
    }

    @PostMapping("/borrow/{bookId}")
    public ResponseEntity<String> borrowBook(@PathVariable int bookId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findUsersByUsername(userDetails.getUsername());
        Book book = bookService.findBookById(bookId);
        user.getBooks().add(book);
        user.setPoints(user.getPoints() + 1);
        userService.updateUser(user);
        return ResponseEntity.ok("Book borrowed successfully.");
    }

    @PostMapping("/return/{bookId}")
    public ResponseEntity<String> returnBook(@PathVariable int bookId,
                             @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findUsersByUsername(userDetails.getUsername());
        Book book = bookService.findBookById(bookId);
        if (user.getBooks().contains(book)) {
            user.getBooks().remove(book);
            userService.updateUser(user);
            return ResponseEntity.ok("Book returned successfully.");
        } else {
            throw new RuntimeException("This book is not in your borrowed list.");
        }
    }

    @GetMapping("/borrowed-books")
    public String showBorrowedBooks(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findUsersByUsername(userDetails.getUsername());
        Set<Book> borrowedBooks = user.getBooks();
        model.addAttribute("borrowedBooks", borrowedBooks);
        return "borrowed-books";
    }

}
