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

    List<Book> filterBooks(String title, String authorName,
                           String genreName, String publisherName, Integer year);

    Book findBookById(int id);

    Book findBookByIsbn(String isbn);

    public Page<Book> findPaginated(Pageable pageable);

    void addBook(Book book);

    void updateBook(Book book);

    void deleteBook(int id);
  
}
