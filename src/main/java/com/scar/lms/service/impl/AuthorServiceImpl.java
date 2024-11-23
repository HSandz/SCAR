package com.scar.lms.service.impl;

import com.scar.lms.entity.Author;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.repository.AuthorRepository;
import com.scar.lms.service.AuthorService;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(final AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Author>> findAllAuthors() {
        return CompletableFuture.completedFuture(authorRepository.findAll());
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Author> findAuthorsByCountry(String country) {
        return authorRepository.findByCountry(country);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Author> findAuthorsByAge(int age) {
        return authorRepository.findByAge(age);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Author findAuthorByEmail(String email) {
        return authorRepository
                .findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Author with email not found" + email));
    }

    @Async
    @Override
    public void addAuthor(Author author) {
        if (authorRepository.findById(author.getId()).isPresent()) {
            throw new DuplicateResourceException("Author with id " + author.getId() + " already exists");
        }
        authorRepository.save(author);
    }

    @Async
    @Override
    public void updateAuthor(Author author) {
        authorRepository.save(author);
    }

    @Async
    @Override
    public void deleteAuthor(int id) {
        var author = authorRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id not found: " + id));
        authorRepository.delete(author);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Author findAuthorById(int id) {
        return authorRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author with id not found" + id));
    }
}
