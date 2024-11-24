package com.scar.lms.controller;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.Borrow;
import com.scar.lms.entity.User;
import com.scar.lms.service.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final BookService bookService;
    private final BorrowService borrowService;
    private final AuthenticationService authenticationService;
    private final CloudStorageService cloudStorageService;

    public UserController(final UserService userService,
                          final BookService bookService,
                          final BorrowService borrowService,
                          final AuthenticationService authenticationService,
                          final CloudStorageService cloudStorageService) {
        this.userService = userService;
        this.bookService = bookService;
        this.borrowService = borrowService;
        this.authenticationService = authenticationService;
        this.cloudStorageService = cloudStorageService;
    }

    @GetMapping("/{userId}/upload")
    public String showUploadForm(@PathVariable int userId, Model model) {
        model.addAttribute("user", userService.findUserById(userId));
        return "upload";
    }

    @PostMapping("/{userId}/upload")
    public String uploadProfileImage(
            @PathVariable int userId,
            @RequestParam("file") MultipartFile file,
            Model model) {
        try {
            User user = userService.findUserById(userId);
            user.setProfilePictureUrl(cloudStorageService.uploadImage(file));
            userService.updateUser(user);

            model.addAttribute("message", "Image uploaded successfully!");
            model.addAttribute("user", user);

        } catch (Exception e) {
            model.addAttribute("message", "Error uploading profile image: " + e.getMessage());
        }

        return "upload";
    }

    @GetMapping({"/", ""})
    public String defaultUserPage() {
        return "redirect:/user/profile";
    }

    @GetMapping("/profile")
    public String showProfilePage(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUserByUsername(username);

        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "error/404";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(Authentication authentication,
                                @RequestParam("username") String updatedUsername,
                                @RequestParam("displayName") String updatedDisplayName,
                                @RequestParam("email") String updatedEmail,
                                Model model) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User currentUser = userService.findUserByUsername(username);
        if (!authenticationService.validateEditProfile(currentUser, updatedUsername, updatedDisplayName, updatedEmail)) {
            model.addAttribute("failure", "Profile not updated.");
        }
        userService.updateUser(currentUser);
        model.addAttribute("success", "Profile updated successfully.");
        return "redirect:/users/profile";
    }

    @GetMapping("/updatePassword")
    public String showUpdatePasswordForm(Model model) {
        model.addAttribute("user", new User());
        return "update-password";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(Authentication authentication,
                                 @RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Model model) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        if (!authenticationService.updatePassword(username, oldPassword, newPassword)) {
            model.addAttribute("error", "Password update failed. Please check your old password and try again.");
            return "update-password";
        }
        model.addAttribute("success", "Password updated successfully.");
        return "redirect:/login";
    }

    @GetMapping("/profile/delete")
    public String showDeleteAccountForm() {
        return "delete-account";
    }

    @PostMapping("/profile/delete")
    public String deleteAccount(Authentication authentication, Model model) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUserByUsername(username);
        userService.deleteUser(user.getId());
        model.addAttribute("success", "Account deleted successfully.");
        return "redirect:/logout";
    }

    @PostMapping("/return/{bookId}")
    public String returnBook(@PathVariable int bookId, Authentication authentication) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUserByUsername(username);

        Borrow borrow = borrowService.findBorrow(user.getId(), bookId)
                .orElseThrow(() -> new RuntimeException("This book is not in your borrowed list."));

        borrow.setReturnDate(LocalDate.now());
        borrowService.updateBorrow(borrow);

        return "redirect:/users/profile";
    }

    @GetMapping("/borrowed-books")
    public String showBorrowedBooks(Authentication authentication, Model model) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUserByUsername(username);
        List<Borrow> borrowedBooks = getBorrowList(user);
        model.addAttribute("borrowedBooks", borrowedBooks);
        return "borrowed-books";
    }

    @PostMapping("/add-favourite/{bookId}")
    public String addFavourite(@PathVariable int bookId, Authentication authentication) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUserByUsername(username);
        userService.addFavouriteFor(user, bookId);
        return "redirect:/book-list/" + bookId;
    }

    @GetMapping("/favourites")
    public String showFavouriteBooks(Authentication authentication, Model model) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUserByUsername(username);
        try {
            CompletableFuture<List<Book>> favouriteBooksFuture = userService.findFavouriteBooks(user.getId());
            List<Book> favouriteBooks = favouriteBooksFuture.join();
            if (favouriteBooks == null) {
                model.addAttribute("error", "Failed to fetch favourite books.");
                return "error/404";
            } else {
                model.addAttribute("favouriteBooks", favouriteBooks);
                return "favourites";
            }
        } catch (Exception e) {
            log.error("Failed to fetch favourite books.", e);
            model.addAttribute("error", "Failed to fetch favourite books.");
            return "error/404";
        }
    }

    @PostMapping("/remove-favourite/{bookId}")
    public String removeFavourite(@PathVariable int bookId, Authentication authentication) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUserByUsername(username);
        userService.removeFavouriteFor(user, bookId);
        return "redirect:/users/favourites";
    }

    @GetMapping("/borrow-history")
    public String showHistory(Authentication authentication, Model model) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUserByUsername(username);
        try {
            List<Borrow> borrows = getBorrowList(user);

            if (borrows == null) {
                model.addAttribute("error", "History not found.");
                return "error/404";
            } else {
                model.addAttribute("history", borrows);
                return "history";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to fetch history.");
            return "error/404";
        }
    }

    private List<Borrow> getBorrowList(User user) {
        CompletableFuture<List<Borrow>> borrowsFuture = borrowService.findBorrowsOfUser(user.getId());
        return borrowsFuture.join();
    }
}
