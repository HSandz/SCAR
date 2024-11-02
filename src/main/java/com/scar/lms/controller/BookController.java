package com.scar.lms.controller;

import com.scar.lms.entity.Book;
import com.scar.lms.service.AuthorService;
import com.scar.lms.service.BookService;
import com.scar.lms.service.GenreService;
import com.scar.lms.service.PublisherService;

import org.springframework.web.bind.annotation.RestController;

@RestController
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
}
