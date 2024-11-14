package com.scar.lms.controller;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.Borrow;
import com.scar.lms.entity.User;
import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.BookService;
import com.scar.lms.service.BorrowService;
import com.scar.lms.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final BookService bookService;
    private final BorrowService borrowService;

    public UserController(final UserService userService,
                          final AuthenticationService authenticationService,
                          final BookService bookService,
                          final BorrowService borrowService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.bookService = bookService;
        this.borrowService = borrowService;
    }

    @GetMapping({"/", ""})
    public String defaultUserPage(Model model) {
        return "redirect:/user/profile";
    }

    @GetMapping("/profile")
    public String showProfilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findUsersByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
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

        if (borrowService.isBookBorrowedBy(user.getId(), bookId)) {
            return ResponseEntity.badRequest().body("You have already borrowed this book.");
        }

        Borrow borrow = new Borrow();
        borrow.setUser(user);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDate.now());

        borrowService.addBorrow(borrow);

        user.setPoints(user.getPoints() + 1);
        userService.updateUser(user);

        return ResponseEntity.ok("Book borrowed successfully.");
    }


    @PostMapping("/return/{bookId}")
    public ResponseEntity<String> returnBook(@PathVariable int bookId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findUsersByUsername(userDetails.getUsername());

        Borrow borrow = borrowService.findBorrow(user.getId(), bookId)
                .orElseThrow(() -> new RuntimeException("This book is not in your borrowed list."));

        borrow.setReturnDate(LocalDate.now());

        borrowService.updateBorrow(borrow);

        return ResponseEntity.ok("Book returned successfully.");
    }


    @GetMapping("/borrowed-books")
    public String showBorrowedBooks(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findUsersByUsername(userDetails.getUsername());

        List<Borrow> borrowedBooks = borrowService.findAllBorrows(user.getId());

        model.addAttribute("borrowedBooks", borrowedBooks);
        return "borrowed-books";
    }
}
