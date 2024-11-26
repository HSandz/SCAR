package com.scar.lms.controller;

import com.scar.lms.entity.*;
import com.scar.lms.service.*;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public BookController(final UserService userService,
                          final BookService bookService,
                          final GoogleBooksService googleBooksService,
                          final AuthenticationService authenticationService,
                          final BorrowService borrowService) {
        this.userService = userService;
        this.bookService = bookService;
        this.googleBooksService = googleBooksService;
        this.authenticationService = authenticationService;
        this.borrowService = borrowService;
    }

    @GetMapping("/api")
    public CompletableFuture<String> searchBooks(@RequestParam(value = "query", defaultValue = "") String query,
                                                 @RequestParam(value = "startIndex", defaultValue = "0") int startIndex,
                                                 @RequestParam(value = "maxResults", defaultValue = "10") int maxResults,
                                                 Model model) {

        CompletableFuture<List<Book>> booksFuture = googleBooksService.searchBooks(query, startIndex, maxResults)
                .thenApply(result -> result.stream()
                        .filter(book -> book.getIsbn() != null)
                        .collect(Collectors.toList()));

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

    @GetMapping({ "", "/" })
    public CompletableFuture<String> findAllBooks(Model model,
                                                  @RequestParam(required = false) String title,
                                                  @RequestParam(required = false) String authorName,
                                                  @RequestParam(required = false) String genreName,
                                                  @RequestParam(required = false) String publisherName,
                                                  @RequestParam(required = false) Integer year,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page - 1, size);

        CompletableFuture<Page<Book>> bookPageFuture = bookService.findFilteredAndPaginated(
                title, authorName, genreName, publisherName, year, pageable);

        CompletableFuture<List<Book>> topBorrowedBooksFuture = bookService.findTopBorrowedBooks();
        CompletableFuture<Long> totalBooksFuture = bookService.countAllBooks();

        return CompletableFuture.allOf(bookPageFuture, topBorrowedBooksFuture, totalBooksFuture)
                .thenApply(_ -> {
                    try {
                        var bookPage = bookPageFuture.get();
                        model.addAttribute("books", bookPage);
                        model.addAttribute("top", topBorrowedBooksFuture.get());
                        model.addAttribute("count", totalBooksFuture.get());

                        int totalPages = bookPage.getTotalPages();
                        if (totalPages > 0) {
                            var pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
                            model.addAttribute("pageNumbers", pageNumbers);
                        }
                    } catch (Exception e) {
                        log.error("Failed to load data: {}", e.getMessage());
                        model.addAttribute("error", "Failed to load data");
                    }
                    return "view-books";
                });
    }

    @GetMapping("/search")
    public CompletableFuture<String> searchBooks(Model model) {
        CompletableFuture<List<Book>> futureBooks = bookService.findAllBooks();
        CompletableFuture<List<Book>> futureTops = bookService.findTopBorrowedBooks();

        return CompletableFuture.allOf(futureBooks, futureTops)
                .thenApply(_ -> {
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
    public CompletableFuture<String> findBookById(@PathVariable("id") int id, Model model) {
        return bookService.findBookById(id)
                .thenApply(book -> {
                    if (book != null) {
                        model.addAttribute("book", book);
                    } else {
                        return "redirect:/error?message=Book+not+found";
                    }
                    return "view-book";
                })
                .exceptionally(e -> {
                    log.error("Failed to load book", e);
                    return "redirect:/error?message=Failed+to+load+book";
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
    public CompletableFuture<String> updateBook(@PathVariable("id") int id, @Valid @ModelAttribute Book book, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return CompletableFuture.completedFuture("update-book");
        }
        book.setId(id);
        return CompletableFuture.runAsync(() -> bookService.updateBook(book))
                .thenApply(_ -> "redirect:/books");
    }

    @GetMapping("/remove/{id}")
    public CompletableFuture<String> deleteBook(@PathVariable("id") int id) {
        return CompletableFuture.runAsync(() -> bookService.deleteBook(id))
                .thenApply(_ -> "redirect:/books");
    }

    @PostMapping("/borrow/{bookId}")
    public CompletableFuture<String> borrowBook(@PathVariable int bookId, Authentication authentication) {
        CompletableFuture<User> userFuture = authenticationService.getAuthenticatedUser(authentication);
        CompletableFuture<Book> bookFuture = bookService.findBookById(bookId);

        return CompletableFuture.allOf(userFuture, bookFuture)
                .thenApply(_ -> {
                    try {
                        extractedBorrowBook(userFuture.get(), bookFuture.get());
                    } catch (Exception e) {
                        log.error("Failed to borrow book", e);
                        return "redirect:/error?message=Failed+to+borrow+book";
                    }
                    return "redirect:/book-list";
                });
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
    public CompletableFuture<String> addBookToDatabase(@Valid @ModelAttribute Book book) {
        book.setDescription(null);
        return CompletableFuture.runAsync(() -> bookService.addBook(book))
                .thenApply(_ -> "api");
    }
}
