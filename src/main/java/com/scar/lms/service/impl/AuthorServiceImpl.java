package com.scar.lms.service.impl;

import com.scar.lms.entity.Author;
import com.scar.lms.exception.NotFoundException;
import com.scar.lms.repository.AuthorRepository;
import com.scar.lms.service.AuthorService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(final AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Author> findAllAuthors() {
        return authorRepository.findAll();
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
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Author with email %s not found", email)
                        )
                );
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Author findAuthorById(int id) {
        return authorRepository
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Author with id %d not found", id)
                        )
                );
    }
}
