package com.scar.lms.service;

import com.scar.lms.entity.Book;

import java.util.List;

public interface BookService {

    List<Book> findAllBooks();

    List<Book> findBooksByTitle(String title);

    List<Book> findBooksByPublicationYear(int year);

    List<Book> searchBook(String keyword);

    Book findBookById(int id);

    Book findBookByIsbn(String isbn);

    void createBook(Book book);

    void updateBook(Book book);

    void deleteBook(int id);

}
