package com.scar.lms.service.impl;

import com.scar.lms.entity.Author;
import com.scar.lms.repository.AuthorRepository;
import com.scar.lms.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(final AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public List<Author> findAllAuthors() {
        return authorRepository.findAll();
    }

    @Override
    public List<Author> findAuthorsByCountry(String country) {
        return authorRepository.findByCountry(country);
    }

    @Override
    public List<Author> findAuthorsByAge(int age) {
        return authorRepository.findByAge(age);
    }

    @Override
    public Author findAuthorByEmail(String email) {
        Optional<Author> authorOptional = authorRepository.findByEmail(email);

        if (authorOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return authorOptional.get();
    }

    @Override
    public Author findAuthorById(int id) {

        Optional<Author> authorOptional = authorRepository.findById(id);

        if (authorOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return authorOptional.get();
    }
}
