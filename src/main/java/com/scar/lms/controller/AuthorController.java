package com.scar.lms.controller;

import com.scar.lms.entity.Author;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.service.AuthorService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(final AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping({"/", ""})
    public String listAllAuthors(Model model) {
        List<Author> authors = authorService.findAllAuthors().join();
        model.addAttribute("authors", authors);
        return "authors";
    }


    @GetMapping("/add")
    public String showAddAuthorForm(Model model) {
        model.addAttribute("author", new Author());
        return "add-author";
    }

    @PostMapping("/add")
    public String addAuthor(@Valid @ModelAttribute Author author, BindingResult result) {
        if (result.hasErrors()) {
            return "add-author";
        }

        authorService.addAuthor(author);
        return "redirect:/admin/authors";
    }
}
