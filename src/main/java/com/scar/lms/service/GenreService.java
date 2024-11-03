package com.scar.lms.service;

import com.scar.lms.entity.Genre;

import java.util.List;

public interface GenreService {

    List<Genre> findAllGenres();

    Genre findGenreById(int id);

    void createGenre(Genre genre);

    void updateGenre(Genre genre);

    void deleteGenre(int id);

}
