package com.scar.lms.controller;

import com.scar.lms.entity.Genre;
import com.scar.lms.service.GenreService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping({"/", ""})
    public String listAllGenre(Model model) {
        model.addAttribute("publishers", genreService.findAllGenres());
        return "genres-list";
    }

    @GetMapping("/add")
    public String showAddGenreForm(Model model) {
        model.addAttribute("genre", new Genre());
        return "add-genre";
    }

    @PostMapping("/add")
    public String addPublisher(@Valid Genre genre, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-genre";
        }
        genreService.createGenre(genre);
        return "redirect:/publishers";
    }
}
