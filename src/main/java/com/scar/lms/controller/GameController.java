package com.scar.lms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scar.lms.entity.Author;
import com.scar.lms.entity.Book;
import com.scar.lms.service.AuthorService;
import com.scar.lms.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/game")
public class GameController {
    private final AuthorService authorService;
    private final BookService bookService;

    public GameController(final AuthorService authorService,
                          final BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @GetMapping("")
    public String showGame(Model model) {
        var authors = authorService.findAllAuthors();
        var books = bookService.findAllBooks();
        CompletableFuture.allOf(authors, books).join();
        try {
            model.addAttribute("authors", authors.get());
            model.addAttribute("books", books.get());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed");
        }
        return "game";
    }
}
