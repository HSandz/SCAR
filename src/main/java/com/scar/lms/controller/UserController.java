package com.scar.lms.controller;

import com.scar.lms.entity.User;
import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.BorrowService;
import com.scar.lms.service.CloudStorageService;
import com.scar.lms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("SameReturnValue")
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
    public CompletableFuture<String> showUploadForm(Authentication authentication, Model model) {
        return getUser(authentication)
                .thenApply(user -> {
                    model.addAttribute("user", user);
                    return "upload";
                });
    }

    @PostMapping("/upload")
    public CompletableFuture<String> uploadProfileImage(
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            Model model) {
        return getUser(authentication)
                .thenCompose(user -> {
                    try {
                        return extractedUploadProfileImage(user.getId(), file, model)
                                .thenApply(_ -> "redirect:/users/profile/edit");
                    } catch (IOException e) {
                        log.error("Error uploading profile image.", e);
                        model.addAttribute("message", "Error uploading profile image: " + e.getMessage());
                        return CompletableFuture.completedFuture("redirect:/users/profile/edit");
                    }
                });
    }

    private CompletableFuture<Void> extractedUploadProfileImage(int userId, MultipartFile file, Model model) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty.");
        } else {
            return userService.findUserById(userId).thenCompose(user -> {
                try {
                    String url = cloudStorageService.uploadImage(file);
                    user.setProfilePictureUrl(url);
                    userService.updateUser(user);
                    model.addAttribute("message", "Image uploaded successfully!");
                    model.addAttribute("user", user);
                    return CompletableFuture.completedFuture(null);
                } catch (IOException e) {
                    log.error("Error uploading profile image.", e);
                    model.addAttribute("message", "Error uploading profile image: " + e.getMessage());
                    return CompletableFuture.completedFuture(null);
                }
            });
        }
    }

    @GetMapping({"/", ""})
    public String defaultUserPage() {
        return "redirect:/user/profile";
    }

    @GetMapping("/profile")
    public CompletableFuture<String> showProfilePage(Model model, Authentication authentication) {
        if (authentication == null) {
            return CompletableFuture.completedFuture("redirect:/login");
        }

        return authenticationService.getAuthenticatedUser(authentication)
                .thenApply(user -> {
                    if (user == null) {
                        model.addAttribute("error", "User not found.");
                        return "error/404";
                    }

                    model.addAttribute("user", user);
                    model.addAttribute("favouriteCount", userService.getFavouriteCount(user.getId()).join());
                    model.addAttribute("borrowCount", borrowService.countBorrowsByUser(user.getId()).join());
                    return "profile";
                });
    }

    @GetMapping("/profile/edit")
    public CompletableFuture<String> showEditProfileForm(Authentication authentication, Model model) {
        return authenticationService.getAuthenticatedUser(authentication)
                .thenApply(user -> {
                    model.addAttribute("user", user);
                    return "edit-user";
                })
                .exceptionally(ex -> {
                    model.addAttribute("error", "Could not retrieve user information. Please try again later.");
                    return "edit-user";
                });
    }

    @PostMapping("/profile/edit")
    public String updateProfile(Authentication authentication,
                                @RequestParam("username") String updatedUsername,
                                @RequestParam("displayName") String updatedDisplayName,
                                @RequestParam("email") String updatedEmail,
                                @RequestParam("aboutMe") String aboutMe,
                                Model model) {
        CompletableFuture<User> userFuture = getUser(authentication);
        User currentUser = userFuture.join();
        if (!authenticationService.validateEditProfile(currentUser, updatedUsername, updatedDisplayName, updatedEmail)) {
            model.addAttribute("failure", "Profile not updated.");
        } else {
            currentUser.setUsername(updatedUsername);
            currentUser.setDisplayName(updatedDisplayName);
            currentUser.setEmail(updatedEmail);
            currentUser.setAboutMe(aboutMe);
            userService.updateUser(currentUser);
            model.addAttribute("success", "Profile updated successfully.");
        }
        return "redirect:/users/profile/edit";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(Authentication authentication,
                                 @RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Model model) {
        try {
            String username = authenticationService.extractUsernameFromAuthentication(authentication).join();
            if (!authenticationService.updatePassword(username, oldPassword, newPassword)) {
                model.addAttribute("error", "Password update failed. Please check your old password and try again.");
            } else {
                model.addAttribute("success", "Password updated successfully.");
            }
        } catch (Exception e) {
            log.error("Failed to update password.", e);
            model.addAttribute("error", "Failed to update password.");
        }
        return "redirect:/users/profile/edit";
    }

    @PostMapping("/profile/delete")
    public CompletableFuture<String> deleteAccount(Authentication authentication, Model model) {
        return getUser(authentication)
                .thenCompose(user -> {
                    userService.deleteUser(user.getId());
                    model.addAttribute("success", "Account deleted successfully.");
                    return CompletableFuture.completedFuture("redirect:/logout");
                });
    }

    @PostMapping("/return/{bookId}")
    public CompletableFuture<ResponseEntity<String>> returnBook(@PathVariable int bookId, Authentication authentication) {
        return getUser(authentication)
                .thenCompose(user -> borrowService.findBorrow(user.getId(), bookId)
                        .thenAccept(borrowOptional -> {
                            borrowOptional.ifPresent(borrow -> {
                                borrow.setReturnDate(LocalDate.now());
                                borrowService.updateBorrow(borrow);
                            });
                        })
                        .thenApply(v -> ResponseEntity.ok("Book returned successfully"))
                        .exceptionally(ex -> ResponseEntity.status(500).body("Failed to return book")));
    }

    @GetMapping("/borrowed-books")
    public CompletableFuture<String> showBorrowedBooks(Authentication authentication, Model model) {
        return getUser(authentication)
                .thenCompose(user -> borrowService.findBorrowsOfUser(user.getId())
                        .thenApply(borrowedBooks -> {
                            model.addAttribute("borrowedBooks", borrowedBooks);
                            return "borrowed-books";
                        })
                        .exceptionally(_ -> {
                            model.addAttribute("error", "Failed to fetch borrowed books.");
                            return "error/404";
                        })
                );
    }

    @GetMapping("/favourites")
    public CompletableFuture<String> showFavouriteBooks(Authentication authentication, Model model) {
        return getUser(authentication)
                .thenCompose(user -> userService.findFavouriteBooks(user.getId())
                        .thenApply(favouriteBooks -> {
                            model.addAttribute("books", favouriteBooks);
                            return "favourites";
                        })
                        .exceptionally(_ -> {
                            model.addAttribute("error", "Failed to fetch favourite books.");
                            return "error/404";
                        })
                );
    }

    @PostMapping("/remove-favourite/{bookId}")
    public CompletableFuture<String> removeFavourite(@PathVariable int bookId, Authentication authentication) {
        return getUser(authentication)
                .thenCompose(user -> {
                    userService.removeFavouriteFor(user, bookId);
                    return CompletableFuture.completedFuture("redirect:/users/favourites");
                });
    }

    @GetMapping("/borrow-history")
    public CompletableFuture<String> showHistory(Authentication authentication, Model model) {
        return getUser(authentication)
                .thenCompose(user -> borrowService.findBorrowsOfUser(user.getId())
                        .thenApply(borrowHistory -> {
                            model.addAttribute("borrowHistory", borrowHistory);
                            return "history";
                        })
                );
    }

    private CompletableFuture<User> getUser(Authentication authentication) {
        return authenticationService.getAuthenticatedUser(authentication);
    }
}
