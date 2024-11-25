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
import java.util.Optional;
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
    private final AuthorService authorService;
    private final GenreService genreService;
    private final PublisherService publisherService;
    private final GoogleBooksService googleBooksService;
    private final AuthenticationService authenticationService;
    private final BorrowService borrowService;

    public BookController(final UserService userService,
                          final BookService bookService,
                          final AuthorService authorService,
                          final GenreService genreService,
                          final PublisherService publisherService,
                          final GoogleBooksService googleBooksService,
                          final AuthenticationService authenticationService,
                          final BorrowService borrowService) {
        this.userService = userService;
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
        this.publisherService = publisherService;
        this.googleBooksService = googleBooksService;
        this.authenticationService = authenticationService;
        this.borrowService = borrowService;
    }

    @GetMapping({ "", "/" })
    public String findAllBooks(Model model,
                               @RequestParam(required = false) String title,
                               @RequestParam(required = false) String authorName,
                               @RequestParam(required = false) String genreName,
                               @RequestParam(required = false) String publisherName,
                               @RequestParam(required = false) Integer year,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page - 1, size); // Page numbers are 1-based

        CompletableFuture<Page<Book>> bookPageFuture = bookService.findFilteredAndPaginated(
                title, authorName, genreName, publisherName, year, pageable);

        CompletableFuture<List<Book>> topBorrowedBooksFuture = bookService.findTopBorrowedBooks();
        CompletableFuture<Long> totalBooksFuture = bookService.countAllBooks();
        CompletableFuture<List<Genre>> genresFuture = genreService.findAllGenres();

        CompletableFuture.allOf(bookPageFuture, topBorrowedBooksFuture, totalBooksFuture, genresFuture).join();

        try {
            var bookPage = bookPageFuture.get();
            model.addAttribute("books", bookPage);
            model.addAttribute("top", topBorrowedBooksFuture.get());
            model.addAttribute("count", totalBooksFuture.get());
            model.addAttribute("genres", genresFuture.get());

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
    }


    @GetMapping("/search")
    public String searchBooks(@RequestParam(required = false, defaultValue = "") String query,
                              @RequestParam(required = false, defaultValue = "0") int page,
                              Model model) {
        int maxResults = 100;
        int pageSize = 10;

        CompletableFuture<List<Book>> futureBooks = googleBooksService.searchBooks(query, page * pageSize, pageSize);

        futureBooks.thenAccept(books -> {
            int totalPages = (int) Math.ceil((double) maxResults / pageSize);

            synchronized (model) {
                model.addAttribute("books", books);
                model.addAttribute("query", query);
                model.addAttribute("currentPage", page);
                model.addAttribute("totalPages", totalPages);
            }
        }).exceptionally(ex -> {
            log.error("Failed to fetch books: {}", ex.getMessage());
            model.addAttribute("error", "Failed to fetch books. Please try again later.");
            return null;
        });

        return "book-list";
    }


    @GetMapping("/{id}")
    public String findBookById(@PathVariable("id") int id, Model model) {
        Optional<Book> bookOptional = Optional.ofNullable(bookService.findBookById(id));
        if (bookOptional.isPresent()) {
            model.addAttribute("book", bookOptional.get());
        } else {
            return "redirect:/error?message=Book+not+found";
        }
        return "view-book";
    }

    @GetMapping("/add")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        addCommonAttributes(model);
        return "add-book";
    }

    @PostMapping("/add")
    public String createBook(@Valid @ModelAttribute Book book, BindingResult result, Model model) {
        if (result.hasErrors()) {
            addCommonAttributes(model);
            return "add-book";
        }
        bookService.addBook(book);
        return "redirect:/books";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable("id") int id, Model model) {
        Optional<Book> bookOptional = Optional.ofNullable(bookService.findBookById(id));
        if (bookOptional.isPresent()) {
            model.addAttribute("book", bookOptional.get());
            return "update-book";
        } else {
            return "redirect:/error?message=Book+not+found";
        }
    }

    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable("id") int id, @Valid @ModelAttribute Book book, BindingResult result, Model model) {
        if (result.hasErrors()) {
            addCommonAttributes(model);
            return "update-book";
        }
        book.setId(id);
        bookService.updateBook(book);
        return "redirect:/books";
    }

    @GetMapping("/remove/{id}")
    public String deleteBook(@PathVariable("id") int id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }

    @PostMapping("/borrow/{bookId}")
    public String borrowBook(@PathVariable int bookId, Authentication authentication) {
        String username = authenticationService.extractUsernameFromAuthentication(authentication);
        User user = userService.findUserByUsername(username);
        Book book = bookService.findBookById(bookId);

        extractedBorrowBook(user, book);

        return "redirect:/book-list";
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
    public String addBookToDatabase(@Valid @ModelAttribute Book book, BindingResult result, Model model) {
        if (result.hasErrors()) {
            addCommonAttributes(model);
            return "book-list";
        }
        bookService.addBook(book);
        return "redirect:/book-list";
    }

    private void addCommonAttributes(Model model) {
        try {
            CompletableFuture<List<Genre>> genresFuture = genreService.findAllGenres();
            CompletableFuture<List<Author>> authorsFuture = authorService.findAllAuthors();
            CompletableFuture<List<Publisher>> publishersFuture = publisherService.findAllPublishers();

            CompletableFuture.allOf(genresFuture, authorsFuture, publishersFuture).join();

            model.addAttribute("genre", genresFuture.get());
            model.addAttribute("authors", authorsFuture.get());
            model.addAttribute("publishers", publishersFuture.get());
        } catch (Exception e) {
            log.error("Failed to load common attributes", e);
            model.addAttribute("error", "Failed to load common attributes");
        }
    }
}