package com.scar.lms.service.impl;

import com.scar.lms.entity.Genre;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.repository.GenreRepository;
import com.scar.lms.service.GenreService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Genre> findAllGenres() {
        return genreRepository.findAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Genre findGenreById(int id) {
        return genreRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre with ID not found: " + id));
    }

    @Override
    public void createGenre(Genre genre) {
        if (genreRepository.existsById(genre.getId())) {
            throw new DuplicateResourceException("Genre with ID " + genre.getId() + " already exists");
        } else if (genreRepository.existsByName(genre.getName())) {
            throw new DuplicateResourceException("Genre with name " + genre.getName() + " already exists");
        }
        genreRepository.save(genre);
    }

    @Override
    public void updateGenre(Genre genre) {
        genreRepository.save(genre);
    }

    @Override
    public void deleteGenre(int id) {
        var genre = genreRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre with ID not found:"  + id));
        genreRepository.delete(genre);
    }
}
