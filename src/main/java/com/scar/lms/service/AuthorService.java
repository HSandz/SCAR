package com.scar.lms.service;

import com.scar.lms.entity.Author;

import java.util.List;

public interface AuthorService {

    List<Author> findAllAuthors();

    List<Author> findAuthorsByCountry(String country);

    List<Author> findAuthorsByAge(int age);

    Author findAuthorById(int id);

    Author findAuthorByEmail(String email);

    void createAuthor(Author author);

    void updateAuthor(Author author);

    void deleteAuthor(int id);

}
