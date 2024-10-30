package com.scar.lms.service.impl;

import com.scar.lms.entity.Book;
import com.scar.lms.entity.Genre;
import com.scar.lms.repository.BookRepository;
import com.scar.lms.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> findBooksByAuthorName(String authorName) {
        return bookRepository.findByAuthorName(authorName);
    }

    @Override
    public List<Book> findBooksByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Override
    public List<Book> findBookByGenre(Genre genre) {
        return bookRepository.findByGenre(genre);
    }

    @Override
    public List<Book> findBooksByPublicationYear(Integer year) {
        return bookRepository.findByPublicationYear(year);
    }

    @Override
    public Book findBookByIsbn(String isbn) {
        Optional<Book> bookOptional = bookRepository.findByIsbn(isbn);

        if (bookOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return bookOptional.get();
    }
}
