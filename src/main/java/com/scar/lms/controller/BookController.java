package com.scar.lms.controller;

import com.scar.lms.service.BookService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    private final BookService bookService;

    public BookController(final BookService bookService) {
        this.bookService = bookService;
    }
}
