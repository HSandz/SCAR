package com.scar.lms.controller;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.Borrow;
import com.scar.lms.entity.Rating;
import com.scar.lms.entity.User;
import com.scar.lms.model.RatingDTO;
import com.scar.lms.service.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("SameReturnValue")
@Slf4j
@Controller
@RequestMapping("/books")
public class BookController {

    private final UserService userService;
    private final BookService bookService;
    private final GoogleBooksService googleBooksService;
    private final AuthenticationService authenticationService;
    private final BorrowService borrowService;
    private final RatingService ratingService;

    public BookController(final UserService userService,
                          final BookService bookService,
                          final GoogleBooksService googleBooksService,
                          final AuthenticationService authenticationService,
                          final BorrowService borrowService,
                          final RatingService ratingService) {
        this.userService = userService;
        this.bookService = bookService;
        this.googleBooksService = googleBooksService;
        this.authenticationService = authenticationService;
        this.borrowService = borrowService;
        this.ratingService = ratingService;
    }

    @GetMapping("/api")
    public CompletableFuture<String> searchAPI(@RequestParam(value = "query", defaultValue = "") String query,
                                               @RequestParam(value = "startIndex", defaultValue = "0") int startIndex,
                                               @RequestParam(value = "maxResults", defaultValue = "40") int maxResults,
                                               Model model) {

        CompletableFuture<List<Book>> booksFuture = googleBooksService.searchBooks(query, startIndex, maxResults)
                .thenApply(ArrayList::new);

        return booksFuture.thenApply(books -> {
            model.addAttribute("books", books);
            model.addAttribute("query", query);
            return "api";
        }).exceptionally(ex -> {
            log.error("Failed to fetch books", ex);
            model.addAttribute("error", "Unable to fetch books. Please try again later.");
            return "api";
        });
    }

    @GetMapping("/search")
    public CompletableFuture<String> searchBooks(Model model,
                                                 @RequestParam(required = false) String title,
                                                 @RequestParam(required = false) String authorName,
                                                 @RequestParam(required = false) String genreName,
                                                 @RequestParam(required = false) String publisherName,
                                                 @RequestParam(required = false) Integer year,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);
        CompletableFuture<Page<Book>> booksFuture = bookService.findFiltered(
                title, authorName, genreName, publisherName, year, pageable);

        CompletableFuture<List<Book>> topBorrowedBooksFuture = bookService.findTopBorrowedBooks();
        CompletableFuture<Long> totalBooksFuture = bookService.countAllBooks();

        return CompletableFuture.allOf(booksFuture, topBorrowedBooksFuture, totalBooksFuture)
                .thenApply(_ -> {
                    try {
                        Page<Book> bookPage = booksFuture.get();
                        model.addAttribute("books", bookPage.getContent());
                        model.addAttribute("currentPage", bookPage.getNumber() + 1);
                        model.addAttribute("totalPages", bookPage.getTotalPages());
                        model.addAttribute("booksPerPage", bookPage.getSize());
                        model.addAttribute("top", topBorrowedBooksFuture.get());
                        model.addAttribute("count", totalBooksFuture.get());
                    } catch (Exception e) {
                        log.error("Failed to load data: {}", e.getMessage());
                        model.addAttribute("error", "Failed to load data");
                    }
                    return "book-search";
                });
    }

    @GetMapping({"", "/"})
    public CompletableFuture<String> findAllBooks(Model model) {
        CompletableFuture<List<Book>> futureBooks = bookService.findAllBooks();
        CompletableFuture<List<Book>> futureTops = bookService.findTopBorrowedBooks();

        return CompletableFuture.allOf(
                futureBooks, futureTops).thenApplyAsync(_ -> {
            try {
                model.addAttribute("books", futureBooks.get());
                model.addAttribute("tops", futureTops.get());
            } catch (Exception ex) {
                log.error("Error occurred while fetching books: {}", ex.getMessage());
                model.addAttribute("error", "Failed to fetch books. Please try again later.");
            }
            return "book-list";
        });
    }

    @GetMapping("/{id}")
    public CompletableFuture<String> findBookById(@PathVariable("id") int id, Model model, Authentication authentication) {
        CompletableFuture<User> userFuture = authenticationService.getAuthenticatedUser(authentication);
        CompletableFuture<Book> bookFuture = bookService.findBookById(id);
        CompletableFuture<List<Rating>> ratingsFuture = ratingService.getBookRatings(id);

        return CompletableFuture.allOf(userFuture, bookFuture, ratingsFuture)
                .thenApply(v -> {
                    User user = userFuture.join();
                    Book book = bookFuture.join();
                    List<Rating> ratings = ratingsFuture.join();

                    model.addAttribute("book", book);
                    model.addAttribute("ratings", ratings);
                    model.addAttribute("user", user);

                    return "book";
                })
                .exceptionally(e -> {
                    log.error("Failed to load book details", e);
                    model.addAttribute("error", "Unable to load book details at the moment.");
                    return "error";
                });
    }

    @PostMapping("/rate/{bookId}")
    public CompletableFuture<ResponseEntity<RatingDTO>> rateBook(@PathVariable int bookId,
                                                                 @RequestParam double points,
                                                                 @RequestParam String comment,
                                                                 Authentication authentication) {
        CompletableFuture<User> userFuture = authenticationService.getAuthenticatedUser(authentication);
        CompletableFuture<Book> bookFuture = bookService.findBookById(bookId);

        return CompletableFuture.allOf(userFuture, bookFuture)
                .thenApply(_ -> {
                    User user = userFuture.join();
                    Book book = bookFuture.join();

                    Rating rating = new Rating();
                    rating.setPoints(points);
                    rating.setComment(comment);
                    rating.setTime(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                    rating.setUser(user);
                    rating.setBook(book);

                    ratingService.saveRating(rating);

                    RatingDTO ratingDto = new RatingDTO(user.getUsername(), comment, points,
                            Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
                    return ResponseEntity.ok(ratingDto);
                })
                .exceptionally(e -> {
                    log.error("Failed to rate book", e);
                    return ResponseEntity.badRequest().body(null);
                });
    }

    @GetMapping("/add")
    public CompletableFuture<String> showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        return CompletableFuture.completedFuture("add-book");
    }

    @PostMapping("/add")
    public CompletableFuture<String> createBook(@Valid @ModelAttribute Book book, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("book", "error");
            return CompletableFuture.completedFuture("add-book");
        }
        return CompletableFuture.runAsync(() -> bookService.addBook(book))
                .thenApply(_ -> "redirect:/books");
    }

    @GetMapping("/update/{id}")
    public CompletableFuture<String> showUpdateForm(@PathVariable("id") int id, Model model) {
        return bookService.findBookById(id)
                .thenApply(book -> {
                    if (book != null) {
                        model.addAttribute("book", book);
                        return "update-book";
                    } else {
                        return "redirect:/error?message=Book+not+found";
                    }
                })
                .exceptionally(e -> {
                    log.error("Failed to load book", e);
                    return "redirect:/error?message=Failed+to+load+book";
                });
    }

    @PostMapping("/update/{id}")
    public CompletableFuture<String> updateBook(@PathVariable("id") int id,
                                                @Valid @ModelAttribute Book book,
                                                BindingResult result) {
        if (result.hasErrors()) {
            return CompletableFuture.completedFuture("update-book");
        }
        book.setId(id);
        return CompletableFuture.runAsync(() -> bookService.updateBook(book))
                .thenApply(_ -> "redirect:/books");
    }

    @DeleteMapping("/remove/{id}")
    public CompletableFuture<String> deleteBook(@PathVariable("id") int id) {
        return CompletableFuture.runAsync(() -> bookService.deleteBook(id))
                .thenApply(_ -> "redirect:/books");
    }

    private void extractedBorrowBook(User user, Book book) {
        Borrow borrow = new Borrow();
        borrow.setUser(user);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDate.now());
        borrowService.addBorrow(borrow);

        user.setPoints(user.getPoints() + 1);
        userService.updateUser(user);
    }

    @PostMapping("/add/db")
    public CompletableFuture<ResponseEntity<String>> addBookToDatabase(@Valid @ModelAttribute Book book) {
        return CompletableFuture.runAsync(() -> bookService.addBook(book))
                .thenApply(_ -> ResponseEntity.ok("Book added successfully"))
                .exceptionally(e -> {
                    log.error("Failed to add book", e);
                    return ResponseEntity.badRequest().body("Failed to add book");
                });
    }

    @PostMapping("/borrow/{bookId}")
    public CompletableFuture<ResponseEntity<String>> borrowBook(@PathVariable int bookId, Authentication authentication) {
        CompletableFuture<User> userFuture = authenticationService.getAuthenticatedUser(authentication);
        CompletableFuture<Book> bookFuture = bookService.findBookById(bookId);

        return CompletableFuture.allOf(userFuture, bookFuture)
                .thenApply(_ -> {
                    User user = userFuture.join();
                    Book book = bookFuture.join();

                    extractedBorrowBook(user, book);
                    return ResponseEntity.ok("Book borrowed successfully");
                })
                .exceptionally(e -> {
                    log.error("Failed to borrow book", e);
                    return ResponseEntity.badRequest().body("Failed to borrow book");
                });
    }

    @DeleteMapping("/return/{bookId}")
    public CompletableFuture<ResponseEntity<String>> returnBook(@PathVariable int bookId, Authentication authentication) {
        CompletableFuture<User> userFuture = authenticationService.getAuthenticatedUser(authentication);
        CompletableFuture<Book> bookFuture = bookService.findBookById(bookId);

        return CompletableFuture.allOf(userFuture, bookFuture)
                .thenApply(_ -> {
                    User user = userFuture.join();
                    Book book = bookFuture.join();

                    user.getBorrows().stream()
                            .filter(borrow -> borrow.getBook().getId() == book.getId())
                            .findFirst()
                            .ifPresent(borrow -> {
                                borrow.setReturnDate(LocalDate.now());
                                borrowService.updateBorrow(borrow);

                                user.setPoints(user.getPoints() - 1);
                                userService.updateUser(user);
                            });

                    return ResponseEntity.ok("Book returned successfully");
                })
                .exceptionally(e -> {
                    log.error("Failed to return book", e);
                    return ResponseEntity.badRequest().body("Failed to return book");
                });
    }

    @PostMapping("/add-favourite/{bookId}")
    public CompletableFuture<ResponseEntity<String>> addFavourite(@PathVariable int bookId, Authentication authentication) {
        CompletableFuture<User> userFuture = authenticationService.getAuthenticatedUser(authentication);
        CompletableFuture<Book> bookFuture = bookService.findBookById(bookId);

        return CompletableFuture.allOf(userFuture, bookFuture)
                .thenApply(_ -> {
                    User user = userFuture.join();
                    Book book = bookFuture.join();

                    user.getFavouriteBooks().add(book);
                    userService.updateUser(user);
                    return ResponseEntity.ok("Book added to favourites");
                })
                .exceptionally(e -> {
                    log.error("Failed to add favourite", e);
                    return ResponseEntity.badRequest().body("Failed to add favourite");
                });
    }

    @DeleteMapping("/remove-favourite/{bookId}")
    public CompletableFuture<ResponseEntity<String>> removeFavourite(@PathVariable int bookId, Authentication authentication) {
        CompletableFuture<User> userFuture = authenticationService.getAuthenticatedUser(authentication);
        CompletableFuture<Book> bookFuture = bookService.findBookById(bookId);

        return CompletableFuture.allOf(userFuture, bookFuture)
                .thenApply(_ -> {
                    User user = userFuture.join();
                    Book book = bookFuture.join();

                    user.getFavouriteBooks().remove(book);
                    userService.updateUser(user);
                    return ResponseEntity.ok("Book removed from favourites");
                })
                .exceptionally(e -> {
                    log.error("Failed to remove favourite", e);
                    return ResponseEntity.badRequest().body("Failed to remove favourite");
                });
    }
}
