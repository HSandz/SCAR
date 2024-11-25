package com.scar.lms.controller;

import com.scar.lms.entity.Genre;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.service.GenreService;

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
@RequestMapping("/admin/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping({"/", ""})
    public String listAllGenre(Model model) {
        List<Genre> genres = genreService.findAllGenres().join();
        model.addAttribute("publishers", genres);
        return "genres";
    }

    @GetMapping("/add")
    public String showAddGenreForm(Model model) {
        model.addAttribute("genre", new Genre());
        return "add-genre";
    }

    @PostMapping("/add")
    public String addPublisher(@Valid @ModelAttribute Genre genre, BindingResult result) {
        if (result.hasErrors()) {
            return "add-genre";
        }
        genreService.createGenre(genre);
        return "redirect:/publishers";
    }
}
