package com.scar.lms.service.impl;

import com.scar.lms.entity.Book;
import com.scar.lms.exception.DuplicateResourceException;
import com.scar.lms.exception.ResourceNotFoundException;
import com.scar.lms.repository.BookRepository;
import com.scar.lms.repository.specification.BookSpecification;
import com.scar.lms.service.BookService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Book>> findAllBooks() {
        return CompletableFuture.supplyAsync(bookRepository::findAll);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> findBooksByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> findBooksByPublicationYear(Integer year) {
        return bookRepository.findByPublicationYear(year);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> findBooksByAuthor(int authorId) {
        return bookRepository.findByAuthor(authorId);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> findBooksByGenre(int genreId) {
        return bookRepository.findByGenre(genreId);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public List<Book> findBooksByPublisher(int publisherId) {
        return bookRepository.findByPublisher(publisherId);
    }

    @Override
    public CompletableFuture<Page<Book>> findPaginated(Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> bookRepository.findAll(pageable));
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Async
    @Override
    public CompletableFuture<Page<Book>> findFilteredAndPaginated(String title,
                                                                  String authorName,
                                                                  String genreName,
                                                                  String publisherName,
                                                                  Integer year,
                                                                  Pageable pageable) {

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

        Specification<Book> finalSpec = spec;
        return CompletableFuture.supplyAsync(() -> bookRepository.findAll(finalSpec, pageable));
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Book findBookById(int id) {
        return bookRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id not found: " + id));
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public Book findBookByIsbn(String isbn) {
        return bookRepository
                .findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book with isbn not found: " + isbn));
    }

    @Async
    @Override
    public void addBook(Book book) {
        if (book.getIsbn() != null && bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new DuplicateResourceException("Book with ISBN " + book.getIsbn() + " already exists");
        } else if (bookRepository.findById(book.getId()).isPresent()) {
            throw new DuplicateResourceException("Book with id " + book.getId() + " already exists");
        } else if (!bookRepository.findByTitle(book.getTitle()).isEmpty()
                && !bookRepository.findByAuthor(book.getAuthor()).isEmpty()) {
            throw new DuplicateResourceException("Book with that title and author already exists");
        }
        bookRepository.save(book);
    }

    @Async
    @Override
    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    @Async
    @Override
    public void deleteBook(int id) {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id not found: " + id));
        bookRepository.delete(book);
    }

    @Async
    @Override
    public CompletableFuture<List<Book>> findTopBorrowedBooks() {
        return CompletableFuture.supplyAsync(bookRepository::findTopBorrowedBooks);
    }

    @Async
    @Override
    public CompletableFuture<Long> countAllBooks() {
        return CompletableFuture.supplyAsync(bookRepository::count);
    }
}
