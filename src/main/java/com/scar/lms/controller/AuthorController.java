package com.scar.lms.controller;

import com.scar.lms.service.AuthorService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(final AuthorService authorService) {
        this.authorService = authorService;
    }
}
