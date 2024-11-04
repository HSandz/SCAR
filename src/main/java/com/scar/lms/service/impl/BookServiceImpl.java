package com.scar.lms.service.impl;

import com.scar.lms.entity.Book;
import com.scar.lms.repository.BookRepository;
import com.scar.lms.repository.specification.BookSpecification;
import com.scar.lms.service.BookService;
import org.springframework.data.jpa.domain.Specification;
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
    public List<Book> findBooksByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Override
    public List<Book> findBooksByPublicationYear(int year) {
        return bookRepository.findByPublicationYear(year);
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword);
    }

    @Override
    public List<Book> filterBooks(String title, String authorName, String genreName, String publisherName, Integer year) {

        Specification<Book> spec = Specification.where(null);

        if (title != null) {
            spec = spec.and(BookSpecification.hasTitle(title));
        }
        if (authorName != null) {
            spec = spec.and(BookSpecification.hasAuthor(authorName));
        }
        if (genreName != null) {
            spec = spec.and(BookSpecification.hasGenre(genreName));
        }
        if (publisherName != null) {
            spec = spec.and(BookSpecification.hasPublisher(publisherName));
        }
        if (year != null) {
            spec = spec.and(BookSpecification.hasYear(year));
        }
        
        return bookRepository.findAll(spec);
    }

    @Override
    public List<Book> findBooksByAuthor(int authorId) {
        return bookRepository.findByAuthor(authorId);
    }

    @Override
    public List<Book> findBooksByGenre(int genreId) {
        return bookRepository.findByGenre(genreId);
    }

    @Override
    public List<Book> findBooksByPublisher(int publisherId) {
        return bookRepository.findByPublisher(publisherId);
    }

    @Override
    public Book findBookById(int id) {

        Optional<Book> bookOptional = bookRepository.findById(id);

        if (bookOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return bookOptional.get();
    }

    @Override
    public Book findBookByIsbn(String isbn) {
        Optional<Book> bookOptional = bookRepository.findByIsbn(isbn);

        if (bookOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return bookOptional.get();
    }

    @Override
    public void addBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public void deleteBook(Book book) {
        bookRepository.delete(book);
    }
}
