package com.scar.lms.service;

import com.scar.lms.entity.Genre;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GenreService {

    CompletableFuture<List<Genre>> findAllGenres();

    CompletableFuture<Genre> findGenreById(int id);

    void createGenre(Genre genre);

    void updateGenre(Genre genre);

    void deleteGenre(int id);
}
