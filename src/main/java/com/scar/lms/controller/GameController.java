package com.scar.lms.controller;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.User;
import com.scar.lms.service.AuthenticationService;
import com.scar.lms.service.BookService;
import com.scar.lms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("SameReturnValue")
@Slf4j
@Controller
@RequestMapping("/game")
public class GameController {

    private final BookService bookService;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    private Book currentBook;
    @SuppressWarnings("FieldCanBeLocal")
    private int maxGuesses;
    private int remainingGuesses;
    private long points;
    private StringBuilder revealedTitle;

    public GameController(final BookService bookService, final UserService userService, AuthenticationService authenticationService) {
        this.bookService = bookService;
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @GetMapping("")
    public String startGame(@RequestParam(defaultValue = "easy") String mode,
                            Model model, Authentication authentication) {

        model.addAttribute("isCorrect", false);
        model.addAttribute("gameOver", false);

        if (authentication == null) {
            model.addAttribute("error", "Please login to play the game.");
            return "login";
        }

        CompletableFuture<List<Book>> booksFuture = bookService.findAllBooks();
        CompletableFuture.allOf(booksFuture).join();

        try {
            List<Book> books = booksFuture.get();

            if (books == null || books.isEmpty()) {
                model.addAttribute("error", "No books available to play the game.");
                return "game";
            }

            Random random = new Random();
            currentBook = books.get(random.nextInt(books.size()));

            if (mode.equalsIgnoreCase("hard")) {
                points = 15;
                maxGuesses = 3;
            } else {
                points = 10;
                maxGuesses = 5;
            }
            remainingGuesses = maxGuesses;

            String title = currentBook.getTitle();
            if (title.isEmpty()) {
                model.addAttribute("error", "The selected book has an invalid title.");
                return "game";
            }

            revealedTitle = new StringBuilder();
            for (char c : title.toCharArray()) {
                if (c == ' ') {
                    revealedTitle.append(' ');
                } else {
                    revealedTitle.append('_');
                }
            }

            model.addAttribute("author", currentBook.getAuthor());
            model.addAttribute("hint", revealedTitle.toString());
            model.addAttribute("points", points);
            model.addAttribute("remainingGuesses", remainingGuesses);
        } catch (Exception e) {
            log.error("Failed to load game data.", e);
            model.addAttribute("error", "Failed to load game data.");
        }

        return "game";
    }

    @PostMapping("/guess")
    public String handleGuess(@RequestParam("guess") String guess,
                              Authentication authentication,
                              Model model) {

        model.addAttribute("isCorrect", false);
        model.addAttribute("gameOver", false);

        if (authentication == null) {
            model.addAttribute("error", "Please login to play the game.");
            return "login";
        }

        if (currentBook == null) {
            model.addAttribute("error", "No active game session. Please start a new game.");
            return "game";
        }

        String correctTitle = currentBook.getTitle();

        try {
            if (guess.equalsIgnoreCase(correctTitle)) {
                CompletableFuture<User> userFuture = authenticationService.getAuthenticatedUser(authentication);
                User user = userFuture.join();

                if (user != null) {
                    long updatedPoints = user.getPoints() + points;
                    user.setPoints(updatedPoints);
                    userService.updateUser(user);

                    model.addAttribute("isCorrect", true);
                    model.addAttribute("correctTitle", correctTitle);
                    model.addAttribute("pointsEarned", points);
                    model.addAttribute("userPoints", user.getPoints());
                } else {
                    model.addAttribute("error", "User not found.");
                }
                return "game-result";
            }

            if (guess.length() == 1) {
                char guessedChar = guess.charAt(0);
                boolean found = false;

                for (int i = 0; i < correctTitle.length(); i++) {
                    if (Character.toLowerCase(correctTitle.charAt(i)) == Character.toLowerCase(guessedChar) &&
                            revealedTitle.charAt(i) == '_') {
                        revealedTitle.setCharAt(i, correctTitle.charAt(i));
                        found = true;
                    }
                }

                if (found) {
                    model.addAttribute("message", "Good guess!");
                } else {
                    remainingGuesses--;
                    points = Math.max(0, points - 1);
                    model.addAttribute("error", "Incorrect guess.");
                }
            } else {
                remainingGuesses--;
                points = Math.max(0, points - 1);
                model.addAttribute("error", "Invalid guess. Please guess a single letter or the full title.");
            }

            model.addAttribute("author", currentBook.getAuthor());
            model.addAttribute("hint", revealedTitle.toString());
            model.addAttribute("remainingGuesses", remainingGuesses);
            model.addAttribute("points", points);

            if (remainingGuesses == 0) {
                model.addAttribute("gameOver", true);
                model.addAttribute("correctTitle", correctTitle);
                model.addAttribute("pointsEarned", points);
                return "game-result";
            }

            if (revealedTitle.toString().equalsIgnoreCase(correctTitle)) {
                model.addAttribute("isCorrect", true);
                model.addAttribute("correctTitle", correctTitle);
                model.addAttribute("pointsEarned", points);
                return "game-result";
            }

        } catch (Exception e) {
            log.error("An unexpected error occurred while handling the guess.", e);
            model.addAttribute("error", "An unexpected error occurred. Please try again.");
        }

        return "game";
    }
}
