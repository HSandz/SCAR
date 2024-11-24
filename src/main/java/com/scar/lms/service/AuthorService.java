package com.scar.lms.service;

import com.scar.lms.entity.Author;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AuthorService {

    CompletableFuture<List<Author>> findAllAuthors();

    List<Author> findAuthorsByCountry(String country);

    List<Author> findAuthorsByAge(int age);

    Author findAuthorById(int id);

    Author findAuthorByEmail(String email);

    void addAuthor(Author author);

    void updateAuthor(Author author);

    void deleteAuthor(int id);
}
