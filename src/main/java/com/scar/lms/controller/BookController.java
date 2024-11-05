package com.scar.lms.controller;

import com.scar.lms.entity.Book;
import com.scar.lms.service.AuthorService;
import com.scar.lms.service.BookService;
import com.scar.lms.service.GenreService;
import com.scar.lms.service.PublisherService;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final PublisherService publisherService;

    public BookController(final BookService bookService,
                          final AuthorService authorService,
                          final GenreService genreService,
                          final PublisherService publisherService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
        this.publisherService = publisherService;
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

        var totalPages = bookPage.getTotalPages();
        if (totalPages > 0) {
            var pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "view-books";
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam("keyword") String keyword, Model model) {
        model.addAttribute("book", bookService.searchBooks(keyword));
        model.addAttribute("keyword", keyword);
        return "view-books";
    }

    @GetMapping("/{id}")
    public String findBookById(@PathVariable("id") int id, Model model) {
        Optional<Book> bookOptional = Optional.ofNullable(bookService.findBookById(id));
        if (bookOptional.isPresent()) {
            model.addAttribute("book", bookOptional.get());
        } else {
            model.addAttribute("error", "Book not found");
        }
        return "view-book";
    }

    @GetMapping("/add")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("genre", genreService.findAllGenres());
        model.addAttribute("authors", authorService.findAllAuthors());
        model.addAttribute("publishers", publisherService.findAllPublishers());
        return "add-book";
    }

    @PostMapping("/add")
    public String createBook(@Valid @ModelAttribute Book book, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("genre", genreService.findAllGenres());
            model.addAttribute("authors", authorService.findAllAuthors());
            model.addAttribute("publishers", publisherService.findAllPublishers());
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
            model.addAttribute("error", "Book not found");
            return "redirect:/books";
        }
    }

    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable("id") int id, @Valid @ModelAttribute Book book, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("genre", genreService.findAllGenres());
            model.addAttribute("authors", authorService.findAllAuthors());
            model.addAttribute("publishers", publisherService.findAllPublishers());
            return "update-book";
        }
        book.setId(id);
        bookService.updateBook(book);
        return "redirect:/books";
    }

    @GetMapping("/remove/{id}")
    public String deleteBook(@PathVariable("id") int id, Model model) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
}
