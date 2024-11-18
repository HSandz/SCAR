package com.scar.lms.service.impl;

import com.scar.lms.entity.Genre;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllGenres() {
        List<Genre> genres = List.of(new Genre(), new Genre());
        when(genreRepository.findAll()).thenReturn(genres);

        List<Genre> result = genreService.findAllGenres();
        assertEquals(2, result.size());
    }

    @Test
    void testFindGenreById() {
        Genre genre = new Genre();
        genre.setId(1);
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));

        Genre result = genreService.findGenreById(1);
        assertEquals(1, result.getId());
    }

    @Test
    void testFindGenreByIdNotFound() {
        when(genreRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> genreService.findGenreById(1));
    }

    @Test
    void testCreateGenre() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Fiction");

        when(genreRepository.existsById(1)).thenReturn(false);
        when(genreRepository.existsByName("Fiction")).thenReturn(false);

        genreService.createGenre(genre);
        verify(genreRepository, times(1)).save(genre);
    }

    @Test
    void testCreateGenreDuplicateId() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Fiction");

        when(genreRepository.existsById(1)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> genreService.createGenre(genre));
    }

    @Test
    void testCreateGenreDuplicateName() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Fiction");

        when(genreRepository.existsById(1)).thenReturn(false);
        when(genreRepository.existsByName("Fiction")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> genreService.createGenre(genre));
    }

    @Test
    void testUpdateGenre() {
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Fiction");

        genreService.updateGenre(genre);
        verify(genreRepository, times(1)).save(genre);
    }

    @Test
    void testDeleteGenre() {
        Genre genre = new Genre();
        genre.setId(1);
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));

        genreService.deleteGenre(1);
        verify(genreRepository, times(1)).delete(genre);
    }

    @Test
    void testDeleteGenreNotFound() {
        when(genreRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> genreService.deleteGenre(1));
    }
}