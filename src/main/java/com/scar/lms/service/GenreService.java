package com.scar.lms.service;

import com.scar.lms.entity.Genre;

import java.util.List;

public interface GenreService {

    public List<Genre> findAllGenres();

    public Genre findGenreById(int id);

    public void createGenre(Genre genre);

    public void updateGenre(Genre genre);

    public void deleteGenre(int id);

}
