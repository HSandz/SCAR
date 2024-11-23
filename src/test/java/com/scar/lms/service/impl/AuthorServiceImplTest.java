package com.scar.lms.service.impl;

import com.scar.lms.entity.Author;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.repository.AuthorRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllAuthors() {
        List<Author> authors = List.of(new Author(), new Author());
        when(authorRepository.findAll()).thenReturn(authors);

        //List<Author> result = authorService.findAllAuthors();
        //assertEquals(authors, result);
    }

    @Test
    void testFindAuthorsByCountry() {
        List<Author> authors = List.of(new Author(), new Author());
        when(authorRepository.findByCountry("USA")).thenReturn(authors);

        List<Author> result = authorService.findAuthorsByCountry("USA");
        assertEquals(authors, result);
    }

    @Test
    void testFindAuthorsByAge() {
        List<Author> authors = List.of(new Author(), new Author());
        when(authorRepository.findByAge(30)).thenReturn(authors);

        List<Author> result = authorService.findAuthorsByAge(30);
        assertEquals(authors, result);
    }

    @Test
    void testFindAuthorByEmail() {
        Author author = new Author();
        when(authorRepository.findByEmail("test@example.com")).thenReturn(Optional.of(author));

        Author result = authorService.findAuthorByEmail("test@example.com");
        assertEquals(author, result);
    }

    @Test
    void testFindAuthorByEmailNotFound() {
        when(authorRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.findAuthorByEmail("test@example.com"));
    }

    @Test
    void testAddAuthor() {
        Author author = new Author();
        when(authorRepository.findById(author.getId())).thenReturn(Optional.empty());

        authorService.addAuthor(author);
        verify(authorRepository, times(1)).save(author);
    }

    @Test
    void testAddAuthorDuplicate() {
        Author author = new Author();
        when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));

        assertThrows(DuplicateResourceException.class, () -> authorService.addAuthor(author));
    }

    @Test
    void testUpdateAuthor() {
        Author author = new Author();

        authorService.updateAuthor(author);
        verify(authorRepository, times(1)).save(author);
    }

    @Test
    void testDeleteAuthor() {
        Author author = new Author();
        when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));

        authorService.deleteAuthor(author.getId());
        verify(authorRepository, times(1)).delete(author);
    }

    @Test
    void testDeleteAuthorNotFound() {
        when(authorRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.deleteAuthor(1));
    }

    @Test
    void testFindAuthorById() {
        Author author = new Author();
        when(authorRepository.findById(1)).thenReturn(Optional.of(author));

        Author result = authorService.findAuthorById(1);
        assertEquals(author, result);
    }

    @Test
    void testFindAuthorByIdNotFound() {
        when(authorRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.findAuthorById(1));
    }
}