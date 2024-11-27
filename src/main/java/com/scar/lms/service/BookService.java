package com.scar.lms.service;

import com.scar.lms.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BookService {

    CompletableFuture<List<Book>> findAllBooks();

    CompletableFuture<List<Book>> findBooksByTitle(String title);

    CompletableFuture<List<Book>> findBooksByPublicationYear(Integer year);

    CompletableFuture<List<Book>> searchBooks(String keyword);

    CompletableFuture<Page<Book>> findFiltered(String title,
                                               String authorName,
                                               String genreName,
                                               String publisherName,
                                               Integer year,
                                               Pageable pageable);

    CompletableFuture<Book> findBookById(int id);

    CompletableFuture<Book> findBookByIsbn(String isbn);

    void addBook(Book book);

    void updateBook(Book book);

    void deleteBook(int id);

    void updateBookRating(int bookId, double rating);

    CompletableFuture<List<Book>> findTopBorrowedBooks();

    CompletableFuture<Long> countAllBooks();

    CompletableFuture<List<Book>> findBooksByGenre(String genre);
}
