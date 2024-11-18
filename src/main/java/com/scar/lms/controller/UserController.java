package com.scar.lms.controller;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.Borrow;
import com.scar.lms.entity.User;
import com.scar.lms.service.*;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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

        String username = authenticationService.getCurrentUsername();
        User user = userService.findUsersByUsername(username);

        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "error/404";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Authentication authentication, Model model) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUsersByUsername(username);
        model.addAttribute("user", user);
        return "editProfile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(Authentication authentication,
                                @ModelAttribute("user") User updatedUser,
                                Model model) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUsersByUsername(username);
        if (authenticationService.validateEditProfile(user, updatedUser)) {
            model.addAttribute("failure", "Profile not updated.");
        }
        userService.updateUser(user);
        model.addAttribute("success", "Profile updated successfully.");
        return "redirect:/users/profile";
    }

    @GetMapping("/updatePassword")
    public String showUpdatePasswordForm(Model model) {
        model.addAttribute("user", new User());
        return "updatePassword";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(Authentication authentication,
                                 @RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Model model) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        if (!authenticationService.updatePassword(username, oldPassword, newPassword)) {
            model.addAttribute("error", "Password update failed. Please check your old password and try again.");
            return "updatePassword";
        }
        model.addAttribute("success", "Password updated successfully.");
        return "redirect:/login";
    }

    @GetMapping("/profile/delete")
    public String showDeleteAccountForm() {
        return "deleteAccount";
    }

    @PostMapping("/profile/delete")
    public String deleteAccount(Authentication authentication, Model model) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUsersByUsername(username);
        userService.deleteUser(user.getId());
        model.addAttribute("success", "Account deleted successfully.");
        return "redirect:/logout";
    }

    @PostMapping("/borrow/{bookId}")
    public String borrowBook(@PathVariable int bookId, Authentication authentication) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUsersByUsername(username);
        Book book = bookService.findBookById(bookId);

        Borrow borrow = new Borrow();
        borrow.setUser(user);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDate.now());

        borrowService.addBorrow(borrow);

        user.setPoints(user.getPoints() + 1);
        userService.updateUser(user);

        return "redirect:/book-list";
    }

    @PostMapping("/return/{bookId}")
    public String returnBook(@PathVariable int bookId, Authentication authentication) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUsersByUsername(username);

        Borrow borrow = borrowService.findBorrow(user.getId(), bookId)
                .orElseThrow(() -> new RuntimeException("This book is not in your borrowed list."));

        borrow.setReturnDate(LocalDate.now());
        borrowService.updateBorrow(borrow);

        return "redirect:/users/profile";
    }

    @GetMapping("/borrowed-books")
    public String showBorrowedBooks(Authentication authentication, Model model) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUsersByUsername(username);
        List<Borrow> borrowedBooks = borrowService.findAllBorrows(user.getId());
        model.addAttribute("borrowedBooks", borrowedBooks);
        return "borrowed-books";
    }

    @PostMapping("/add-favourite/{bookId}")
    public String addFavourite(@PathVariable int bookId, Authentication authentication) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUsersByUsername(username);
        userService.addFavouriteFor(user, bookId);
        return "redirect:/book-list/" + bookId;
    }

    @GetMapping("/favourites")
    public String showFavouriteBooks(Authentication authentication, Model model) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUsersByUsername(username);
        Set<Book> favouriteBooks = userService.findFavouriteBooks(user.getId());
        model.addAttribute("favouriteBooks", favouriteBooks);
        return "favourite-books";
    }

    @PostMapping("/remove-favourite/{bookId}")
    public String removeFavourite(@PathVariable int bookId, Authentication authentication) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUsersByUsername(username);
        userService.removeFavouriteFor(user, bookId);
        return "redirect:/user/favourites";
    }
}
