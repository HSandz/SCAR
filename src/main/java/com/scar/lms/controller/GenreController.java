package com.scar.lms.controller;

import com.scar.lms.entity.Genre;
import com.scar.lms.service.GenreService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SuppressWarnings("SameReturnValue")
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
    public String addGenre(@Valid @ModelAttribute Genre genre, BindingResult result) {
        if (result.hasErrors()) {
            return "add-genre";
        }
        genreService.createGenre(genre);
        return "redirect:/genres";
    }

    @GetMapping("/update/{genreId}")
    public String showUpdateGenreForm(@PathVariable int genreId, Model model) {
        try {
            Genre genre = genreService.findGenreById(genreId).join();
            model.addAttribute("genre", genre);
        } catch (Exception e) {
            model.addAttribute("error", "Genre not found.");
        }
        return "update-genre";
    }

    @PostMapping("/update")
    public String updateGenre(@Valid @ModelAttribute Genre genre, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("genre", genre);
            return "update-genre";
        }

        extractedUpdateGenre(genre);
        return "redirect:/genres";
    }

    private void extractedUpdateGenre(Genre genre) {
        Genre updatedGenre = genreService.findGenreById(genre.getId()).join();
        updatedGenre.setName(genre.getName());
        updatedGenre.setBooks(genre.getBooks());
        genreService.updateGenre(genre);
    }
}
