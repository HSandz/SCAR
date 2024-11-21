package com.scar.lms.controller;

import com.scar.lms.entity.Book;
import com.scar.lms.service.*;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final PublisherService publisherService;
    private final GoogleBooksService googleBooksService;

    public BookController(final BookService bookService,
                          final AuthorService authorService,
                          final GenreService genreService,
                          final PublisherService publisherService,
                          final GoogleBooksService googleBooksService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
        this.publisherService = publisherService;
        this.googleBooksService = googleBooksService;
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

        Pageable pageable = PageRequest.of(page, size);

        var bookPage = bookService.findFilteredAndPaginated
                (title, authorName, genreName, publisherName, year, pageable);

        model.addAttribute("books", bookPage);
        model.addAttribute("top", bookService.findTopBorrowedBooks());
        model.addAttribute("count", bookService.findAllBooks().size());

        var totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            var pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "view-books";
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam(required = false, defaultValue = "") String query,
                              @RequestParam(required = false, defaultValue = "0") int page,
                              Model model) {
        int maxResults = 100;
        int pageSize = 10;
        List<Book> books = googleBooksService.searchBooks(query, page * pageSize, pageSize);
        int totalPages = (int) Math.ceil((double) maxResults / pageSize);

        model.addAttribute("books", books);
        model.addAttribute("query", query);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

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
        model.addAttribute("genre", genreService.findAllGenres());
        model.addAttribute("authors", authorService.findAllAuthors());
        model.addAttribute("publishers", publisherService.findAllPublishers());
    }
}