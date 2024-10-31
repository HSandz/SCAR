package com.scar.lms.service;

import com.scar.lms.entity.Book;

import java.util.List;

public interface BookService {

    List<Book> findAllBooks();

    List<Book> findBooksByAuthor(Integer authorId);

    List<Book> findBooksByTitle(String title);

    List<Book> findBooksByGenre(Integer genreId);

    List<Book> findBooksByPublicationYear(Integer year);

    List<Book> searchBook(String keyword);

    Book findBookByIsbn(String isbn);
}
