package com.scar.lms.service;

import com.scar.lms.entity.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {

    List<Book> findAllBooks();

    List<Book> findBooksByTitle(String title);

    List<Book> findBooksByPublicationYear(int year);

    List<Book> findBooksByAuthor(int authorId);

    List<Book> findBooksByGenre(int genreId);

    List<Book> findBooksByPublisher(int publisherId);

    List<Book> searchBooks(String keyword);

    Page<Book> findPaginated(Pageable pageable);

    Page<Book> findFilteredAndPaginated(String title,
                                        String authorName,
                                        String genreName,
                                        String publisherName,
                                        Integer year,
                                        Pageable pageable);

    Book findBookById(int id);

    Book findBookByIsbn(String isbn);

    void addBook(Book book);

    void updateBook(Book book);

    void deleteBook(int id);
  
}
