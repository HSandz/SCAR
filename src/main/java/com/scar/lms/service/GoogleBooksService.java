package com.scar.lms.service;

import com.scar.lms.entity.Book;

import java.util.List;

public interface GoogleBooksService {

    List<Book> searchBooks(String query);
}
