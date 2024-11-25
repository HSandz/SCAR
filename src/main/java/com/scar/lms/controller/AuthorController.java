package com.scar.lms.controller;

import com.scar.lms.entity.Author;
import com.scar.lms.service.AuthorService;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("SameReturnValue")
@Slf4j
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

    @GetMapping("/update/{authorId}")
    public String updateAuthorInfoForm(@PathVariable int authorId, Model model) {
        try {
            CompletableFuture<Author> authorFuture = authorService.findAuthorById(authorId);
            Author author = authorFuture.join();
            model.addAttribute("author", author);
            return "redirect:/admin/authors";
        } catch (Exception e) {
            model.addAttribute("error", "Author not found.");
            return "error/404";
        }
    }

    @PostMapping("/update")
    public String updateAuthorInfo(@Valid @ModelAttribute Author author, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("author", author);
            return "update-author";
        }

        extractedUpdateAuthorInfo(author);
        return "redirect:/admin/authors";
    }

    private void extractedUpdateAuthorInfo(Author author) {
        try {
            Author updatedAuthor = authorService.findAuthorById(author.getId()).join();

            updatedAuthor.setDescription(author.getDescription());
            updatedAuthor.setName(author.getName());
            updatedAuthor.setCountry(author.getCountry());
            updatedAuthor.setAge(author.getAge());
            updatedAuthor.setEmail(author.getEmail());

            authorService.updateAuthor(updatedAuthor);
        } catch (Exception e) {
            log.error("Failed to update author.", e);
        }
    }
}
