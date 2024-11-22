package com.scar.lms.controller;

import com.scar.lms.entity.Author;
import com.scar.lms.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(final AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping({"/", ""})
    public String listAllAuthors(Model model) {
        model.addAttribute("authors", authorService.findAllAuthors());
        return "authors-list";
    }

    @GetMapping("/add/author")
    public String showAddAuthorForm(Model model) {
        model.addAttribute("author", new Author()); // Assuming you have an Author for form binding
        return "add-author"; // Points to the Thymeleaf template 'add-author.html'
    }

    @PostMapping("/add/author")
    public String addAuthor(@Valid Author author, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-author"; // Show the form again if validation fails
        }

        authorService.addAuthor(author); // Save the new author via the service
        return "redirect:/authors"; // Redirect to the list of authors after successful save
    }
}
