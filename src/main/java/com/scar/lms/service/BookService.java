package com.scar.lms.service;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.Genre;

import java.util.List;
import java.util.Optional;

public interface BookService {

    List<Book> findAllBooks();

    List<Book> findBooksByAuthorName(String authorName);

    List<Book> findBooksByTitle(String title);

    List<Book> findBookByGenre(Genre genre);

    List<Book> findBooksByPublicationYear(Integer year);

    Book findBookByIsbn(String isbn);
}
