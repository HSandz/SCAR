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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final BorrowService borrowService;
    private final AuthenticationService authenticationService;
    private final CloudStorageService cloudStorageService;

    public UserController(final UserService userService,
                          final BorrowService borrowService,
                          final AuthenticationService authenticationService,
                          final CloudStorageService cloudStorageService) {
        this.userService = userService;
        this.borrowService = borrowService;
        this.authenticationService = authenticationService;
        this.cloudStorageService = cloudStorageService;
    }

    @GetMapping("/upload")
    public String showUploadForm(
            Authentication authentication,
            Model model) {
        int userId = userService.findUserByUsername(
                authenticationService.extractUsernameFromAuthentication(authentication)).getId();
        model.addAttribute("user", userService.findUserById(userId));
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadProfileImage(
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            Model model) {
        int userId = userService.findUserByUsername(
                authenticationService.extractUsernameFromAuthentication(authentication)).getId();
        try {
            User user = userService.findUserById(userId);
            user.setProfilePictureUrl(cloudStorageService.uploadImage(file));
            userService.updateUser(user);

            model.addAttribute("message", "Image uploaded successfully!");
            model.addAttribute("user", user);

        } catch (Exception e) {
            log.error("Error uploading profile image.", e);
            model.addAttribute("message", "Error uploading profile image: " + e.getMessage());
        }

        return "profile";
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

        User user = authenticationService.getAuthenticatedUser(authentication);

        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "error/404";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Authentication authentication, Model model) {
        User user = authenticationService.getAuthenticatedUser(authentication);
        model.addAttribute("user", user);
        return "edit-user";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(Authentication authentication,
                                @RequestParam("username") String updatedUsername,
                                @RequestParam("displayName") String updatedDisplayName,
                                @RequestParam("email") String updatedEmail,
                                Model model) {
        User currentUser = authenticationService.getAuthenticatedUser(authentication);
        if (!authenticationService.validateEditProfile(currentUser, updatedUsername, updatedDisplayName, updatedEmail)) {
            model.addAttribute("failure", "Profile not updated.");
        }
        userService.updateUser(currentUser);
        model.addAttribute("success", "Profile updated successfully.");
        return "redirect:/users/profile/edit";
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
        User user = authenticationService.getAuthenticatedUser(authentication);
        userService.deleteUser(user.getId());
        model.addAttribute("success", "Account deleted successfully.");
        return "redirect:/logout";
    }

    @PostMapping("/return/{bookId}")
    public String returnBook(@PathVariable int bookId, Authentication authentication) {
        User user = authenticationService.getAuthenticatedUser(authentication);

        try {
            CompletableFuture<Optional<Borrow>> borrowOptionalFuture = borrowService.findBorrow(user.getId(), bookId);
            Optional<Borrow> borrowOptional = borrowOptionalFuture.join();

            if (borrowOptional.isPresent()) {
                Borrow borrow = borrowOptional.get();
                borrow.setReturnDate(LocalDate.now());
                borrowService.updateBorrow(borrow);
            }
            return "redirect:/users/borrowed-books";
        } catch (Exception e) {
            log.error("Failed to return book.", e);
            return "redirect:/users/borrowed-books";
        }
    }

    @GetMapping("/borrowed-books")
    public String showBorrowedBooks(Authentication authentication, Model model) {
        User user = authenticationService.getAuthenticatedUser(authentication);
        List<Borrow> borrowedBooks = getBorrowList(user);
        model.addAttribute("borrowedBooks", borrowedBooks);
        return "borrowed-books";
    }

    @PostMapping("/add-favourite/{bookId}")
    public String addFavourite(@PathVariable int bookId, Authentication authentication) {
        User user = authenticationService.getAuthenticatedUser(authentication);
        userService.addFavouriteFor(user, bookId);
        return "redirect:/book-list/" + bookId;
    }

    @GetMapping("/favourites")
    public String showFavouriteBooks(Authentication authentication, Model model) {
        User user = authenticationService.getAuthenticatedUser(authentication);
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
        User user = authenticationService.getAuthenticatedUser(authentication);
        userService.removeFavouriteFor(user, bookId);
        return "redirect:/users/favourites";
    }


    @GetMapping("/borrow-history")
    public String showHistory(Authentication authentication, Model model) {
        User user = authenticationService.getAuthenticatedUser(authentication);
        List<Borrow> borrowHistory = getBorrowList(user);
        model.addAttribute("borrowHistory", borrowHistory);
        return "history";
    }

    private List<Borrow> getBorrowList(User user) {
        CompletableFuture<List<Borrow>> borrowsFuture = borrowService.findBorrowsOfUser(user.getId());
        return borrowsFuture.join();
    }
}
