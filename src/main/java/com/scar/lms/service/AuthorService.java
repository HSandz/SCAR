package com.scar.lms.service;

import com.scar.lms.entity.Author;

import java.util.List;

public interface AuthorService {

    List<Author> findAllAuthors();

    List<Author> findAuthorsByCountry(String country);

    List<Author> findAuthorsByAge(Integer age);

    Author findAuthorByEmail(String email);
}
