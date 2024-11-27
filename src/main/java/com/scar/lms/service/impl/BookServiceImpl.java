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

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Book>> findAllBooks() {
        return CompletableFuture.supplyAsync(bookRepository::findAll);
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Book>> findBooksByTitle(String title) {
        return CompletableFuture.supplyAsync(() -> bookRepository.findByTitle(title));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Book>> findBooksByPublicationYear(Integer year) {
        return CompletableFuture.supplyAsync(() -> bookRepository.findByPublicationYear(year));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Book>> searchBooks(String keyword) {
        return CompletableFuture.supplyAsync(() -> bookRepository.searchBooks(keyword));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<Page<Book>> findFiltered(String title,
                                                      String authorName,
                                                      String genreName,
                                                      String publisherName,
                                                      Integer year,
                                                      Pageable pageable) {

        Specification<Book> spec = Specification.where(null);

        spec = getSpecification(title, authorName, genreName, publisherName, year, spec);

        Specification<Book> finalSpec = spec;
        return CompletableFuture.supplyAsync(() -> bookRepository.findAll(finalSpec, pageable));
    }

    private Specification<Book> getSpecification(String title,
                                                 String authorName,
                                                 String genreName,
                                                 String publisherName,
                                                 Integer year, Specification<Book> spec) {
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
        return spec;
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<Book> findBookById(int id) {
        return bookRepository
                .findById(id)
                .map(CompletableFuture::completedFuture)
                .orElse(CompletableFuture.failedFuture(new ResourceNotFoundException("Book with id not found: " + id)));
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<Book> findBookByIsbn(String isbn) {
        return bookRepository
                .findByIsbn(isbn)
                .map(CompletableFuture::completedFuture)
                .orElse(CompletableFuture.failedFuture(
                        new ResourceNotFoundException("Book with ISBN not found: " + isbn))
                );
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
    public void updateBookRating(int bookId, double rating) {
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id not found: " + bookId));
        book.setRating(rating);
        bookRepository.save(book);
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<List<Book>> findTopBorrowedBooks() {
        return CompletableFuture.supplyAsync(bookRepository::findTopBorrowedBooks);
    }

    @Async
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    @Override
    public CompletableFuture<Long> countAllBooks() {
        return CompletableFuture.supplyAsync(bookRepository::count);
    }

    @Override
    public CompletableFuture<List<Book>> findBooksByGenre(String genre) {
        return CompletableFuture.supplyAsync(() -> bookRepository.findByGenre(genre));
    }
}
