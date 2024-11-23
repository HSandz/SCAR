package com.scar.lms.service;

import com.scar.lms.entity.Book;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GoogleBooksService {

    CompletableFuture<List<Book>> searchBooks(String query, int startIndex, int maxResults);
}
